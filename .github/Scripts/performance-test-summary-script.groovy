import groovy.xml.MarkupBuilder

String timestamp = System.env.TIMESTAMP
String jtlFilePath = ".github/JMeterPerformanceTest/Results/Reports/${timestamp}_Report/${timestamp}_testresults.jtl"
String outputXmlPath = ".github/JMeterPerformanceTest/Results/Reports/${timestamp}_Report/${timestamp}_summary_report.xml"

int warningThreshold = 200  // Warning threshold in milliseconds
int slaThreshold = 300      // SLA threshold in milliseconds
def jtlFile = new File(jtlFilePath)

if (!jtlFile.exists()) {
    println "JTL file not found: $jtlFilePath"
    return
}

List<Map<String, Object>> requestData = []
Map<String, Object> aggregatedData = [:]

def calculatePercentile(total, count) {
    return (count / (double) total * 100).round(2)
}

jtlFile.eachLine { line ->
    if (!line.startsWith("timeStamp")) {
        def parts = line.split(/,(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)/)
        def elapsed = Integer.parseInt(parts[1])
        def responseCode = Integer.parseInt(parts[3])

        def label = parts[2]
        aggregatedData[label] = aggregatedData.getOrDefault(label, [totalResponseTime: 0, totalWithinSLA: 0, totalWarnings: 0, totalFailures: 0, totalRequests: 0])
        aggregatedData[label].totalResponseTime += elapsed
        aggregatedData[label].totalWithinSLA += elapsed <= slaThreshold ? 1 : 0
        aggregatedData[label].totalWarnings += (elapsed > warningThreshold && elapsed < slaThreshold) ? 1 : 0
        aggregatedData[label].totalFailures += elapsed > slaThreshold ? 1 : 0
        aggregatedData[label].totalRequests++

        def warningMessage = elapsed > warningThreshold && elapsed < slaThreshold ? "Warning: The operation took longer than 200 ms." : ""
        def failureMessage = elapsed > slaThreshold ? "Failure: The operation lasted too long. It took ${elapsed} milliseconds, exceeding the SLA of 300 milliseconds." : ""

        def successPercentile = calculatePercentile(aggregatedData[label].totalRequests, aggregatedData[label].totalWithinSLA).toString() + "%"
        def failurePercentile = calculatePercentile(aggregatedData[label].totalRequests, aggregatedData[label].totalFailures).toString() + "%"

        def requestInfo = [
            timeStamp: parts[0],
            elapsed: parts[1],
            label: label,
            responseCode: parts[3],
            responseMessage: parts[4],
            assertHttpCode: responseCode != 200 ? "Request was not successful! ${responseCode}" : "Request successful",
            dataType: parts[6],
            success: parts[7],
            warningMessage: warningMessage,
            failureMessage: failureMessage,
            bytes: parts[9],
            sentBytes: parts[10],
            grpThreads: parts[11],
            allThreads: parts[12],
            URL: parts[13],
            latency: parts[14],
            idleTime: parts[15],
            connect: parts[16]
        ]
        requestData << requestInfo
    }
}

def writer = new StringWriter()
def xml = new MarkupBuilder(writer)

xml.summaryReport {
    requestData.each { requestInfo ->
        request(label: requestInfo.label) {
            requestInfo.each { key, value ->
                if (key != 'label') {
                    "$key"(value ?: 'None')
                }
            }
        }
    }
    summarized {
        aggregatedData.each { label, data ->
            totalRequests(data.totalRequests)
            totalResponseTime(data.totalResponseTime + " ms")
            totalWithinSLA(data.totalWithinSLA)
            totalWarnings("⚠️: " + data.totalWarnings)
            totalFailures("❌: " + data.totalFailures)
            successPercentile(calculatePercentile(data.totalRequests, data.totalWithinSLA).toString() + "%")
            failurePercentile(calculatePercentile(data.totalRequests, data.totalFailures).toString() + "%")
        }
    }
}

new File(outputXmlPath).withWriter('UTF-8') { it.write(writer.toString()) }
println "XML report written to: $outputXmlPath"
