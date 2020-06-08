package org.mgreau.csv2html

import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine;
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction;

import org.apache.commons.csv.*

class Csv2Html extends DefaultTask {

    /** Directory that contains CSV files */
    @InputDirectory
    File csvFilesDir

    /** Path to generate the HTML Report with all CSV files content */
    @OutputDirectory
    File htmlReportDir

    @TaskAction
    void renderCSVasHTMLReport() {
        displayCsvFiles()
        generateHTMLReport()
    }

    protected void displayCsvFiles(){
        for(String f : csvFilesDir.list()){
            println(f)
        }
    }

    protected void printCsvContent(){
        for(String csvfilename : (new FileNameByRegexFinder().getFileNames(csvFilesDir.path, /.*\.csv/))){
            this.parse(new BufferedReader(new FileReader(new File(csvfilename)), true));
        }
    }

    private long parse(final Reader input, final boolean traverseColumns){
        final CSVFormat format = CSVFormat.RFC4180.withFirstRecordAsHeader();
        long recordCount = 0;
        for (final CSVRecord record : format.parse(input)) {
            recordCount++;
            if (traverseColumns) {
                println('-----------------')
                    print(record.get("name") + '|')
                    print(record.get("version") + '|')
                println('-----------------')
            }
        }
        return recordCount;
    }


    private void generateHTMLReport(){

        File output = new File(htmlReportDir, 'report.html')

        Map<String, Object> depsReport = new HashMap<>()

        final CSVFormat format = CSVFormat.RFC4180.withFirstRecordAsHeader();
        for(String csvfilename : (new FileNameByRegexFinder().getFileNames(csvFilesDir.path, /.*\.csv/))){
            depsReport.put(csvfilename.split("/").last(), format.parse(new BufferedReader(new FileReader(new File(csvfilename)))))
        }

        VelocityEngine velocityEngine = new VelocityEngine()
        velocityEngine.setProperty("runtime.references.strict", true)
        velocityEngine.init()
        VelocityContext context = new VelocityContext()
        context.put("report", depsReport)

        evaluateTemplate(output, velocityEngine, context, 'template-report.html')
    }

    protected static void evaluateTemplate(File outputFile, VelocityEngine velocityEngine, VelocityContext context, String template) {
        StringWriter out = new StringWriter()
        Reader templateReader = null
        try {
            println(template)
            templateReader = Csv2HtmlPlugin.getResourceAsStream(template).newReader('UTF-8')
            velocityEngine.evaluate(context, out, "", templateReader)
        } finally {
            if (templateReader != null) {
                templateReader.close()
            }
        }
        outputFile.setText(out.toString(), 'UTF-8')
    }

}