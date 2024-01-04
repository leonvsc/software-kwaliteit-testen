import groovy.xml.MarkupBuilder

String timestamp = System.env.TIMESTAMP
String jtlFilePath = ".github/JMeterTestPlan/Results/Reports/${timestamp}_Report/${timestamp}_testresults.jtl"
String outputXmlPath = ".github/JMeterTestPlan/Results/Reports/${timestamp}_Report/${timestamp}_summary_report.xml" // Path to output XML file

// Open the JTL file
def jtlFile = new File(jtlFilePath)
if (!jtlFile.exists()) {
    println "JTL file not found: $jtlFilePath"
    return
}

List<Map<String, String>> requestData = []

// Process each line in the JTL file
jtlFile.eachLine { line ->
    if (!line.startsWith("timeStamp")) {
        def parts = line.split(',')
        def request = [
            timeStamp: parts[0],
            elapsed: parts[1],
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
            connect: parts[16]
        ]
        requestData << request
    }
}

// Create XML content
def writer = new StringWriter()
def xml = new MarkupBuilder(writer)
xml.requests {
    requestData.each { request ->
        request.each { key, value ->
            "$key"(value ?: 'None')
        }
    }
}

// Save the XML content to a file
new File(outputXmlPath).withWriter('UTF-8') { fileWriter ->
    fileWriter.write(writer.toString())
}

println "XML report written to: $outputXmlPath"