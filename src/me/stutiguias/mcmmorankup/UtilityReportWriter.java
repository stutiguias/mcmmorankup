package me.stutiguias.mcmmorankup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;

import com.google.common.collect.Multimap;

public class UtilityReportWriter {

    private static Mcmmorankup plugin;

    public UtilityReportWriter(Mcmmorankup instance) {
        plugin = instance;
    }
    
    public static String SaveReportToFile(Multimap<String, String> report, String FileName, String category) {
        FileWriter fileWriter;
        Date date = new Date();
        String reportName;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yy_hh-mm");

        if (category == null) {
            reportName = "skill_";
        } else {
            if (category.equalsIgnoreCase("G")) {
                reportName = "gender";
            } else {
                reportName = category;
            }
        }

        if (FileName != null) {
            reportName = FileName;
        } else {
            reportName += "_report-" + dateFormat.format(date);
        }

        String fileNamePath = Mcmmorankup.PluginReportsDir + File.separator + reportName + ".txt";

        String content = (report.size() > 0 ? GetReportStructureBuilder(report) : null);
        if (content == null) {
            System.out.println("Report/File contents was empty aborting writer...");
            return null;
        }

        try {
            content += "\n\n\n* = Rank was purchased";
            content += "\ngenerated: " + date + " - filename: " + fileNamePath;
            File newTextFile = new File(fileNamePath);
            fileWriter = new FileWriter(newTextFile);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
            return fileNamePath;
        } catch (IOException ioex) {
            Mcmmorankup.logger.log(Level.SEVERE, "{0} Error: {1}", new Object[]{Mcmmorankup.logPrefix, ioex.getMessage()});
            return null;
        } catch (NullPointerException npe) {
            Mcmmorankup.logger.log(Level.SEVERE, "{0} Null Error: {1}", new Object[]{Mcmmorankup.logPrefix, npe.getStackTrace()});
            return null;
        }
    }

    public static String GetReportStructureBuilder(Multimap<String, String> report) {

        StringBuilder reportContent = new StringBuilder();
        String category = "";

        for (String key : report.keySet()) {
            if (report.get(key) == null) continue;
            if (!key.equalsIgnoreCase(category)) {
                if (category.isEmpty()) {
                    reportContent.append(plugin.MessageSeparator).append("\n");
                }
                category = key;
            }

            Collection<String> categoryValues = report.get(category);
            for (String value : categoryValues) {
                reportContent.append(" » ").append(category.toUpperCase()).append("     ").append(value).append("\n");
            }
        }

        return reportContent.toString();
    }
}
