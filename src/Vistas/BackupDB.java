package Vistas;

import java.awt.HeadlessException;
import java.io.IOException;
import java.sql.Connection;
import javax.swing.JOptionPane;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class BackupDB {
    
    public  String db = "bydeauiwkosfltg2tbfh";
    public  String url = "mysql://u6xichrqiyoa9cgd:NWt7bgrogWXno0sb6BJG@bydeauiwkosfltg2tbfh-mysql.services.clever-cloud.com:3306/bydeauiwkosfltg2tbfh";
    public  String user = "u6xichrqiyoa9cgd";
    public  String password = "NWt7bgrogWXno0sb6BJG";
    public String port = "3306";
    public String Host = "bydeauiwkosfltg2tbfh-mysql.services.clever-cloud.com";
    String[] Acciones = {"Generar Backup","Restaurar Backup"};
    public String desktopPath = System.getProperty("user.home") + "/Downloads/JavaApplication10/Backups DB/";
    String mysqldumpPath = "C:/xampp/mysql/bin/mysqldump.exe";

    private final int IDUsuario;
    public final Connection cx;
    String Accion;
    
    public BackupDB(int IDUsuario, Connection Conexion, String TipoAccion){
        
        this.IDUsuario = IDUsuario;       
        this.cx = Conexion;
        this.Accion = TipoAccion;
    }
    
    public void Decision(){
    


    if("Generar Backup".equals(Accion)){
        Generar_Backup();}
    else{
        Restaurar_Backup();}}
    
    public void Generar_Backup(){
    
    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    String backupFile = desktopPath + "backup_" + timestamp + ".sql";

    try {
        ProcessBuilder pb = new ProcessBuilder(
            mysqldumpPath,
            "-h", Host,
            "-P", port,
            "-u", user,
            "--password=" + password,
            "--no-tablespaces",
            db
        );

        pb.redirectErrorStream(false);
        Process p = pb.start();

        //  <-- AQUÍ SE ESCRIBE EL DUMP EXACTO, SIN ALTERAR NADA
        try (java.io.InputStream is = p.getInputStream();
             java.io.FileOutputStream fos = new java.io.FileOutputStream(backupFile)) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }

        int exit = p.waitFor();

        if (exit == 0) {
            JOptionPane.showMessageDialog(null, "Backup creado exitosamente:\n" + backupFile);
        } else {
            JOptionPane.showMessageDialog(null,
                "Backup creado con advertencias (exit=" + exit + ").",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
        }

    } catch (HeadlessException | IOException | InterruptedException e) {
        JOptionPane.showMessageDialog(null, "Error inesperado:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }    
        
    }
   
    
    public void Restaurar_Backup(){
        
            try {
            // Selector de archivos
            javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser(desktopPath);
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos SQL (*.sql)", "sql"));
            fileChooser.setDialogTitle("Selecciona el archivo SQL");

            int opcion = fileChooser.showOpenDialog(null);
            if (opcion != javax.swing.JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(null, "Restauración cancelada.");
                return;
            }

            java.io.File archivoBackup = fileChooser.getSelectedFile();
            String backupPath = archivoBackup.getAbsolutePath();

            // Confirmar
            int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "¿Restaurar este backup?\n" + backupPath,
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            String mysqlPath = "C:/xampp/mysql/bin/mysql.exe";

            ProcessBuilder pb = new ProcessBuilder(
                mysqlPath,
                "-h", Host,
                "-P", port,
                "-u", user,
                "--password=" + password,
                db
            );

            pb.redirectErrorStream(true);
            pb.redirectInput(ProcessBuilder.Redirect.from(new java.io.File(backupPath)));

            Process p = pb.start();

            // <-- LEEMOS EL ERROR EN TEXTO
            java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(p.getInputStream())
            );

            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exit = p.waitFor();

            if (exit == 0) {
                JOptionPane.showMessageDialog(null, "Base de datos restaurada exitosamente.");
            } else {
                JOptionPane.showMessageDialog(null,
                        "Código: " + exit + "\n\nSalida del proceso:\n" + output,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error:\n" + e.getMessage());
        }
        
    }
    
    

    
    
}
