import groovy.xml.MarkupBuilder

String timestamp = System.env.TIMESTAMP
String jtlFilePath = ".github/JMeterTestPlan/Results/Reports/${timestamp}_Report/${timestamp}_testresults.jtl"
String outputXmlPath = ".github/JMeterTestPlan/Results/Reports/${timestamp}_Report/${timestamp}_summary_report.xml"

int warningThreshold = 200  // Define the warning threshold in milliseconds
def jtlFile = new File(jtlFilePath)

if (!jtlFile.exists()) {
    println "JTL file not found: $jtlFilePath"
    return
}

List<Map<String, Object>> requestData = []
Map<String, Object> aggregatedData = [:]

jtlFile.eachLine { line ->
    if (!line.startsWith("timeStamp")) {
        def parts = line.split(',')
        def elapsed = Integer.parseInt(parts[1])
        def warningCount = (elapsed > warningThreshold && elapsed < 300) ? 1 : 0

        def label = parts[2]
        aggregatedData[label] = aggregatedData.getOrDefault(label, [totalResponseTime: 0, totalWarnings: 0, totalRequests: 0])
        aggregatedData[label].totalResponseTime += elapsed
        aggregatedData[label].totalWarnings += warningCount
        aggregatedData[label].totalRequests++

        def requestInfo = [
            timeStamp: parts[0],
            elapsed: parts[1],
            label: label,
            responseCode: parts[3],
            responseMessage: parts[4],
            assertHttpCode: (responseCode != 200) ? "Request was not successful! ${responseCode}",
            threadName: parts[5],
            dataType: parts[6],
            success: parts[7],
            failureMessage: parts[8],
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
            totalRequestSummary {
                totalResponseTime(data.totalResponseTime)
                totalWarnings(data.totalWarnings)
                totalRequests(data.totalRequests)
            }
        }
    }
}

new File(outputXmlPath).withWriter('UTF-8') { it.write(writer.toString()) }
println "XML report written to: $outputXmlPath"
