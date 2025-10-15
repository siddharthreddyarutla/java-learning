package com.example.javaLearning.designPatterns.structural.adapter;

public class Adapter {

  public static void main(String[] args) {

    ReportGenerator csvGenerator = new ReportGeneratorAdapter(new CSVExporter());
    csvGenerator.exportFile();

    ReportGenerator xlsvGenerator = new XLSVExporter();
    xlsvGenerator.exportFile();
  }


  // Target interface
  public interface ReportGenerator {
    void exportFile();
  }


  // Adaptee
  public static class CSVExporter {

    public void exportCSV() {
      System.out.println("Exporting CSV file in progress....");
      try {
        Thread.sleep(2000);
      } catch (Exception e) {
        System.out.println(e);
      }
      System.out.println("CSV file is generated");
    }
  }


  // Adapter makes Adaptee compatible with Target
  public static class ReportGeneratorAdapter implements ReportGenerator {

    private final CSVExporter csvExporter;

    public ReportGeneratorAdapter(CSVExporter csvExporter) {
      this.csvExporter = csvExporter;
    }

    @Override
    public void exportFile() {
      this.csvExporter.exportCSV();
    }
  }


  public static class XLSVExporter implements ReportGenerator {

    @Override
    public void exportFile() {
      System.out.println("Exporting XLSV file in progress....");
      try {
        Thread.sleep(2000);
      } catch (Exception e) {
        System.out.println(e);
      }
      System.out.println("XLSV file is generated");
    }
  }
}
