import groovy.xml.MarkupBuilder
String timestamp = System.env.TIMESTAMP
String jtlFilePath = ".github/JMeterTestPlan/Results/Reports/${timestamp}_Report/${timestamp}_testresults.jtl"
String outputXmlPath = ".github/JMeterTestPlan/Results/Reports/${timestamp}_Report/${timestamp}_summary_report.xml" // Path to output XML file

// Define the warning threshold in milliseconds
int warningThreshold = 200

// Open the JTL file
def jtlFile = new File(jtlFilePath)
if (!jtlFile.exists()) {
    println "JTL file not found: $jtlFilePath"
    return
}

// Define a map to hold the summary data
Map<String, Map<String, Object>> summary = [:]

// Process each line in the JTL file
jtlFile.eachLine { line ->
    if (!line.startsWith("timeStamp")) { // Skip the header line
        def parts = line.split(',')
        def label = parts[2]
        def responseTime = Integer.parseInt(parts[1])



        // Initialize data structure for each label
        if (!summary.containsKey(label)) {
            summary[label] = [totalRequests: 0, totalResponseTime: 0, warningCount: 0]
        }

        // Update summary data
        summary[label].totalRequests++
        summary[label].totalResponseTime += responseTime

        // Check if response time exceeds the warning threshold
        if (responseTime > warningThreshold) {
            summary[label].warningCount++
        }

        def entry = [
            timeStamp: parts[0],
            elapsed: responseTime,
            label: parts[2],
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
        summary << entry
    }
}

// Create XML content
def writer = new StringWriter()
def xml = new MarkupBuilder(writer)
xml.summaryReport {
    summary.each { label, data ->
        request(label: label) {
            totalRequests(data.totalRequests)
            averageResponseTime("${data.totalResponseTime / data.totalRequests} ms")
            warningsCount(data.warningCount)
            
            timeStamp(request.timeStamp)
            elapsed(request.elapsed)
            label(request.label)
            responseCode(request.responseCode)
            responseMessage(request.responseMessage)
            threadName(request.threadName)
            dataType(request.dataType)
            success(request.success)
            failureMessage(request.failureMessage ?: 'None')
            bytes(request.bytes)
            sentBytes(request.sentBytes)
            grpThreads(request.grpThreads)
            allThreads(request.allThreads)
            URL(request.URL ?: 'None')
            latency(request.latency)
            idleTime(request.idleTime)
            connect(request.connect ?: 'None')
            warning(request.warning)
        }
    }
}

// Save the XML content to a file
new File(outputXmlPath).withWriter('UTF-8') { fileWriter ->
    fileWriter.write(writer.toString())
}

println "Summary report written to: $outputXmlPath"