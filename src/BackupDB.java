import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.io.IOException;
import java.sql.Connection;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class BackupDB {
    
    public  String db;
    public  String url;
    public  String user;
    public  String password;
    public String port;
    public String Host;
    String[] Acciones = {"Generar Backup","Restaurar Backup"};
    public String desktopPath = System.getProperty("user.home") + "/Downloads/JavaApplication10/Backups DB/";
    String mysqldumpPath = "C:/xampp/mysql/bin/mysqldump.exe";

    private final int IDUsuario;
    public final Connection cx;
    
    public BackupDB(String url, String db, String password, Connection cx,
                         String user, int IDUsuario, String port, String Host){
        
        this.IDUsuario = IDUsuario;
        this.user = user;
        this.db = db;
        this.url = url;
        this.password = password;
        this.cx = cx;
        this.port = port;
        this.Host = Host;
        
    
    }
    
    public void Decision(){
    boolean eleccion = false;
    String Accion;
    while(!eleccion){
    JPanel Infraestructura_DB = new JPanel(new GridLayout(2, 2));
    JComboBox<String> comboboxAcciones = new JComboBox<>(Acciones);
    Infraestructura_DB.add(new JLabel("Escoja la accion: "));
    Infraestructura_DB.add(comboboxAcciones);
    Infraestructura_DB.add(new JLabel("Estos procesos pueden tardar"));
    Infraestructura_DB.add(new JLabel("  Agradecemos su paciencia"));
   
    
     int BackupDecisivo = JOptionPane.showConfirmDialog(
            null,
            Infraestructura_DB,
            "Escoja una opcion para proceder!!!",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);
         
          if (BackupDecisivo != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Modificacion de la infraestructura cancelada con exito ", "Error", JOptionPane.WARNING_MESSAGE);
            return;}
    
                Accion = (String) comboboxAcciones.getSelectedItem();
                
                if(Accion == null || Accion.trim().isEmpty()){
                    //Mensaje de Advertencia
                JOptionPane.showMessageDialog(null, "Debe escoger una opcion");
                }

               if("Generar Backup".equals(Accion)){
               Generar_Backup();
               }
               else{
               Restaurar_Backup();
               }

               eleccion = true;
                } }
    
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
