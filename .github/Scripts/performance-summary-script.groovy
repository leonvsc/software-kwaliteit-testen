import groovy.xml.MarkupBuilder

String timestamp = System.getenv('TIMESTAMP')
String jtlFilePath = ".github/JMeterTestPlan/Results/Reports/${timestamp}_Report/${timestamp}_testresults.jtl"
String outputXmlPath = ".github/JMeterTestPlan/Results/Reports/${timestamp}_Report/${timestamp}_summary_report.xml"

// Define the warning threshold in milliseconds
int warningThreshold = 200

// Open the JTL file
def jtlFile = new File(jtlFilePath)
if (!jtlFile.exists()) {
    println "JTL file not found: $jtlFilePath"
    return
}

Map<String, Map<String, Object>> summary = [:]
List<Map<String, Object>> requestData = []

jtlFile.eachLine { line ->
    if (!line.startsWith("timeStamp")) {
        def parts = line.split(',')
        def responseTime = Integer.parseInt(parts[1])
        def label = parts[2]
        def warning = responseTime > warningThreshold

        summary[label] = summary.getOrDefault(label, [totalRequests: 0, totalResponseTime: 0, warningCount: 0])
        summary[label].totalRequests++
        summary[label].totalResponseTime += responseTime
        if (warning) {
            summary[label].warningCount++
        }

        def entry = [
            timeStamp: parts[0],
            elapsed: responseTime,
            label: label,
            responseCode: parts[3],
            responseMessage: parts[4],
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
            connect: parts[16],
            warning: warning
        ]
        requestData << entry
    }
}

def writer = new StringWriter()
def xml = new MarkupBuilder(writer)
xml.summaryReport {
    summary.forEach { label, data ->
        'requestSummary'(label: label) {
            'totalRequests'(data.totalRequests)
            'averageResponseTime'("${data.totalResponseTime / data.totalRequests} ms")
            'warningsCount'(data.warningCount)
        }
    }
    'detailedRequests' {
        requestData.forEach { request ->
            'request' {
                request.each { key, value ->
                    "$key"(value ?: 'None')
                }
            }
        }
    }
}

new File(outputXmlPath).withWriter('UTF-8') { fileWriter ->
    fileWriter.write(writer.toString())
}

println "Summary report written to: $outputXmlPath"
