/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package chafaint;

import java.util.ArrayList; 
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import java.io.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.Timer;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

/**
 *
 * @author rescamilla
 */
public class Main extends javax.swing.JFrame {
    // --- VARIABLES GLOBALES ---
    private ArrayList<Nodo> listaPuntos = new ArrayList<>();
    private Color currentColor = Color.BLACK;
    private int currentSize = 10;
    private boolean poligonoCerrado = false; 
    private static final String APP_VERSION = "0.3.4";
    private static final String APP_AUTHOR = "rescamilla";
    private LienzoPanel lienzo;
    private javax.swing.JPopupMenu menuContextual;

    // Variables para selección y modificación
    private int puntoSeleccionado = -1; // -1 significa ninguno seleccionado
    private boolean arrastrandoPunto = false;
    private int puntoArrastrando = -1;

    // Variables manuales (Por seguridad)
    private javax.swing.JLabel jTxtPositionX;
    private javax.swing.JLabel jTxtPositionY;
    private javax.swing.JLabel jTxtColorCode;
    
    class LienzoPanel extends JPanel {
        public LienzoPanel() {
            this.setBackground(Color.WHITE); // Fondo blanco para dibujar
        }
       
        
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); 
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

            if (!listaPuntos.isEmpty()) {
                for (int i = 0; i < listaPuntos.size(); i++) {
                    Nodo actual = listaPuntos.get(i);
                    
                    // Dibujar punto NORMAL o SELECCIONADO
                    if (i == puntoSeleccionado) {
                        g2.setColor(Color.RED); // Punto seleccionado en ROJO
                        g2.fillOval(actual.x - 5, actual.y - 5, 10, 10);
                    } else {
                        g2.setColor(actual.color);
                        g2.fillOval(actual.x - 3, actual.y - 3, 6, 6);
                    }
                    
                    // Dibujar líneas entre puntos
                    if (i < listaPuntos.size() - 1) {
                        Nodo sig = listaPuntos.get(i + 1);
                        g2.setStroke(new BasicStroke(actual.grosor)); 
                        g2.setColor(actual.color); 
                        g2.drawLine(actual.x, actual.y, sig.x, sig.y);
                    }
                }
                
                // Cerrar polígono si está cerrado
                if (poligonoCerrado && listaPuntos.size() > 2) {
                    Nodo ult = listaPuntos.get(listaPuntos.size() - 1);
                    Nodo pri = listaPuntos.get(0);
                    g2.setStroke(new BasicStroke(ult.grosor)); 
                    g2.setColor(ult.color);
                    g2.drawLine(ult.x, ult.y, pri.x, pri.y);
                }
            }
        }
    }

    // CLASE NODO
    class Nodo {
        int x, y; Color color; int grosor;
        public Nodo(int x, int y, Color color, int grosor) {
            this.x = x; this.y = y; this.color = color; this.grosor = grosor;
        }
    }
    
    // Logger
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Main.class.getName());
    
    /**
     * Creates new form Main
     */
public Main() {
    initComponents();
    
    // Evitar que las toolbars se puedan arrastrar
    jToolBar1.setFloatable(false);
    jToolBar2.setFloatable(false);

    Mode.setText("Modo: Asignación");

    // Inicializar etiquetas manuales (Evita NullPointerException)
    jTxtPositionX = new javax.swing.JLabel("X: 0");
    jTxtPositionY = new javax.swing.JLabel("Y: 0");
    jTxtColorCode = new javax.swing.JLabel("Color");
    jLabelVersion.setText("Versión: " + APP_VERSION);    

    // Agregarlas a la barra inferior manualmente
    jToolBar2.add(new javax.swing.JToolBar.Separator());
    jToolBar2.add(jTxtPositionX);
    jToolBar2.add(new javax.swing.JToolBar.Separator());
    jToolBar2.add(jTxtPositionY);
    
    // --- RELOJ ---
    Timer timer = new Timer(1000, e -> {
        LocalDateTime now = LocalDateTime.now();
        if(jLabelDay != null) jLabelDay.setText("Día: " + now.toLocalDate());
        if(jLabelHour != null) jLabelHour.setText("Hora: " + now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    });
    timer.start();
    
    // --- LIENZO ---
    // Cambiar el layout del contentPane a BorderLayout
    getContentPane().setLayout(new BorderLayout());
    
    // Agregar los toolbars en sus posiciones
    getContentPane().add(jToolBar1, BorderLayout.NORTH);
    getContentPane().add(jToolBar2, BorderLayout.SOUTH);
    
    // Crear el lienzo
    lienzo = new LienzoPanel();
    
    // Agregar el lienzo al centro
    getContentPane().add(lienzo, BorderLayout.CENTER);
    
// Crear menú contextual
menuContextual = new javax.swing.JPopupMenu();
javax.swing.JMenuItem menuBorrar = new javax.swing.JMenuItem("Borrar Punto");
javax.swing.JMenuItem menuModificar = new javax.swing.JMenuItem("Modificar Punto");
javax.swing.JMenuItem menuCerrar = new javax.swing.JMenuItem("Cerrar Figura");
    
    menuBorrar.addActionListener(e -> borrarPuntoSeleccionado());
menuModificar.addActionListener(e -> {
    if (puntoSeleccionado != -1) {
        Nodo punto = listaPuntos.get(puntoSeleccionado);
        String xStr = JOptionPane.showInputDialog(this, "Nueva coordenada X:", punto.x);
        String yStr = JOptionPane.showInputDialog(this, "Nueva coordenada Y:", punto.y);

        if (xStr != null && yStr != null) {
            try {
                int nuevoX = Integer.parseInt(xStr);
                int nuevoY = Integer.parseInt(yStr);
                // CLAMPEAR para que no se salga del panel
                punto.x = Math.max(0, Math.min(lienzo.getWidth() - 1, nuevoX));
                punto.y = Math.max(0, Math.min(lienzo.getHeight() - 1, nuevoY));

                if (Mode.getText().equals("Modo: Archivo")) {
                    Mode.setText("Modo: Modificación");
                }

                lienzo.repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Coordenadas inválidas");
            }
        }
    }
});
    
    menuCerrar.addActionListener(e -> {
        if (listaPuntos.size() > 2) {
            if (!poligonoCerrado) {
                poligonoCerrado = true; 
                JOptionPane.showMessageDialog(Main.this, "Figura Cerrada");
            } else {
                JOptionPane.showMessageDialog(Main.this, "Ya está cerrada");
            }
            lienzo.repaint();
        } else {
            JOptionPane.showMessageDialog(Main.this, "Necesitas 3 puntos mínimo.");
        }
    });
    
    menuContextual.add(menuBorrar);
    menuContextual.add(menuModificar);
    menuContextual.add(new javax.swing.JPopupMenu.Separator());
    menuContextual.add(menuCerrar);
    
    // Eventos del Mouse
    javax.swing.event.MouseInputAdapter mouseHandler = new javax.swing.event.MouseInputAdapter() {
@Override
public void mousePressed(java.awt.event.MouseEvent evt) {
    // --- Menú contextual (para Linux/Mac) ---
    if (evt.isPopupTrigger()) {
        mostrarMenuContextual(evt);
        return;
    }

    // --- Arrastrar punto seleccionado ---
    if (puntoSeleccionado != -1 && javax.swing.SwingUtilities.isLeftMouseButton(evt)) {
        Nodo punto = listaPuntos.get(puntoSeleccionado);
        double distancia = Math.sqrt(Math.pow(evt.getX() - punto.x, 2) + Math.pow(evt.getY() - punto.y, 2));
        if (distancia <= 10) {
            puntoArrastrando = puntoSeleccionado;
            arrastrandoPunto = true;
        }
    }
}
        
        @Override
        public void mouseMoved(java.awt.event.MouseEvent evt) { 
            actualizarCoordenadas(evt); 
        }
        
        @Override
        public void mouseDragged(java.awt.event.MouseEvent evt) {
            // Si estamos arrastrando un punto, actualizar sus coordenadas
            if (arrastrandoPunto && puntoArrastrando != -1) {
                Nodo punto = listaPuntos.get(puntoArrastrando);
// Dentro de mouseDragged
punto.x = Math.max(0, Math.min(lienzo.getWidth() - 1, evt.getX()));
punto.y = Math.max(0, Math.min(lienzo.getHeight() - 1, evt.getY()));
                
                // Cambiar a Modificación si estaba en Archivo
                if (Mode.getText().equals("Modo: Archivo")) {
                    Mode.setText("Modo: Modificación");
                }
                
                lienzo.repaint();
            }
        }
        
@Override
public void mouseReleased(java.awt.event.MouseEvent evt) {
    // --- Finalizar arrastre ---
    if (arrastrandoPunto) {
        // Asegurar que el punto esté dentro del lienzo
        Nodo punto = listaPuntos.get(puntoArrastrando);
        punto.x = Math.max(0, Math.min(lienzo.getWidth() - 1, punto.x));
        punto.y = Math.max(0, Math.min(lienzo.getHeight() - 1, punto.y));
        
        Mode.setText("Modo: Modificación");
        arrastrandoPunto = false;
        puntoArrastrando = -1;
        lienzo.repaint();
    }

    // --- Menú contextual (Windows) ---
    if (evt.isPopupTrigger()) {
        mostrarMenuContextual(evt);
    }
}
        
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            // Click izquierdo: agregar punto
if (javax.swing.SwingUtilities.isLeftMouseButton(evt) && evt.getClickCount() == 1) {
                listaPuntos.add(new Nodo(evt.getX(), evt.getY(), currentColor, currentSize));
                jLabelElements.setText("Elem: " + listaPuntos.size());
                
                // Si ya estaba cerrado, ahora está MODIFICADO y ya no cerrado
                if (poligonoCerrado) {
                    poligonoCerrado = false;
                    Mode.setText("Modo: Modificación");
                }
                // Cambiar a Modificación si estaba en Archivo
                else if (Mode.getText().equals("Modo: Archivo")) {
                    Mode.setText("Modo: Modificación");
                }
                
                lienzo.repaint();
            }
            // Click derecho: cerrar figura (solo si NO fue sobre un punto)
            else if (javax.swing.SwingUtilities.isRightMouseButton(evt)) {
                // Verificar que NO fue sobre un punto (ya que eso muestra menú contextual)
                int puntoCerca = encontrarPuntoCercano(evt.getX(), evt.getY(), 10);
                
                if (puntoCerca == -1) { // Solo cerrar si fue en espacio vacío
                    if (listaPuntos.size() > 2) {
                        if (!poligonoCerrado) {
                            poligonoCerrado = true; 
                            JOptionPane.showMessageDialog(Main.this, "Figura Cerrada");
                        } else {
                            JOptionPane.showMessageDialog(Main.this, "Ya está cerrada");
                        }
                        lienzo.repaint();
                    } else {
                        JOptionPane.showMessageDialog(Main.this, "Necesitas 3 puntos mínimo.");
                    }
                }
                // Si fue sobre un punto, ya se manejó en mouseReleased (menú contextual)
            }
            
            // Doble-click: seleccionar punto
            if (evt.getClickCount() == 2 && javax.swing.SwingUtilities.isLeftMouseButton(evt)) {
                seleccionarPunto(evt.getX(), evt.getY());
            }
        }
    };
    
    lienzo.addMouseListener(mouseHandler);
    lienzo.addMouseMotionListener(mouseHandler);
    

    
// --- Tecla DELETE global (funciona SIEMPRE, sin importar el foco) ---
KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
    if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_DELETE) {
        borrarPuntoSeleccionado();
        return true;
    }
    return false;
});
    

addWindowListener(new java.awt.event.WindowAdapter() {
    @Override
    public void windowClosing(java.awt.event.WindowEvent e) {
        SALIR();
    }
});
    
    // Configurar tamaño
    setSize(800, 600);
    setLocationRelativeTo(null);
}




    // --- MÉTODOS DE LÓGICA (EL CEREBRO) ---

    private void actualizarCoordenadas(java.awt.event.MouseEvent evt) {
        if(jTxtPositionX != null) jTxtPositionX.setText("X: " + evt.getX());
        if(jTxtPositionY != null) jTxtPositionY.setText("Y: " + evt.getY());
    }


    private void mostrarMenuContextual(java.awt.event.MouseEvent evt) {
    int puntoCerca = encontrarPuntoCercano(evt.getX(), evt.getY(), 10);
    if (puntoCerca != -1) {
        puntoSeleccionado = puntoCerca;
        lienzo.repaint();
        menuContextual.show(lienzo, evt.getX(), evt.getY());
    }
}
    
private Boolean guardarArchivo() {
    JFileChooser fc = new JFileChooser();
    fc.setFileFilter(new FileNameExtensionFilter("Archivos de Puntos (.pts)", "pts"));

    if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        File f = fc.getSelectedFile();
        if (!f.getName().toLowerCase().endsWith(".pts")) {
            f = new File(f.getParentFile(), f.getName() + ".pts");
        }
        // --- Escritura segura: primero a temporal ---
        File temp = new File(f.getAbsolutePath() + ".tmp");
        try (PrintWriter pw = new PrintWriter(temp)) {
            pw.println("CHAFAINT_V1");
            for (Nodo n : listaPuntos) {
                pw.println(n.x + ";" + n.y + ";" + n.color.getRed() + "," + n.color.getGreen() + "," + n.color.getBlue() + ";" + n.grosor);
            }
            pw.flush();
            // Reemplazar archivo original
            if (f.exists()) f.delete();
            temp.renameTo(f);

            JOptionPane.showMessageDialog(this, "Guardado exitoso");
            Mode.setText("Modo: Archivo");
            return Boolean.TRUE;   // Éxito
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage());
            return Boolean.FALSE;  // Error real
        }
    } else {
        return null;  // Usuario canceló
    }
}

    private void abrirArchivo() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Archivos de Puntos (.pts)", "pts"));
        
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader br = new BufferedReader(new FileReader(fc.getSelectedFile()))) {
                if (!"CHAFAINT_V1".equals(br.readLine())) {
                    JOptionPane.showMessageDialog(this, "Archivo incompatible"); 
                    return;
                }
                listaPuntos.clear();
                String line;
                while ((line = br.readLine()) != null) {
    try {
        String[] p = line.split(";");
        if (p.length != 4) continue; // línea mal formada, saltar
        
        int x = Integer.parseInt(p[0]);
        int y = Integer.parseInt(p[1]);
        
        String[] c = p[2].split(",");
        if (c.length != 3) continue;
        
        int r = Integer.parseInt(c[0]);
        int g = Integer.parseInt(c[1]);
        int b = Integer.parseInt(c[2]);
        // Clampear RGB 0-255
        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));
        
        int grosor = Integer.parseInt(p[3]);
        if (grosor <= 0) grosor = 1; // mínimo 1
        
        listaPuntos.add(new Nodo(x, y, new Color(r, g, b), grosor));
    } catch (NumberFormatException e) {
        // ignorar línea corrupta
    }
}
                poligonoCerrado = false; 
                puntoSeleccionado = -1;
                jLabelElements.setText("Elem: " + listaPuntos.size());
                Mode.setText("Modo: Archivo");  // DESPUÉS de cargar los datos
                lienzo.repaint();  
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error al leer"); }
        }
    }
    
    private void limpiarLienzo() { 
        listaPuntos.clear(); 
        poligonoCerrado = false; 
        puntoSeleccionado = -1;
        lienzo.repaint();
        jLabelElements.setText("Elem: 0"); 
        Mode.setText("Modo: Asignación");
    }
    
    private void ColorSelector() {
        Color c = javax.swing.JColorChooser.showDialog(this, "Color", currentColor);
        if(c!=null) currentColor = c;
    }
    
private void SizeSelector() {
    String s = JOptionPane.showInputDialog(this, "Grosor (1-50):", currentSize);
    if (s != null) {
        try {
            int nuevo = Integer.parseInt(s);
            if (nuevo >= 1 && nuevo <= 50) {
                currentSize = nuevo;
            } else {
                JOptionPane.showMessageDialog(this, "El grosor debe estar entre 1 y 50.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un número entero.");
        }
    }
}

    private void mostrarValores() {
    StringBuilder sb = new StringBuilder("Puntos:\n");
    for (int i = 0; i < listaPuntos.size(); i++) {
        Nodo n = listaPuntos.get(i);
        sb.append(i + 1).append(". X=").append(n.x).append(" Y=").append(n.y)
          .append(" Color=RGB(").append(n.color.getRed()).append(",")
          .append(n.color.getGreen()).append(",").append(n.color.getBlue())
          .append(") Grosor=").append(n.grosor).append("\n");
    }
    javax.swing.JTextArea textArea = new javax.swing.JTextArea(sb.toString());
    textArea.setEditable(false);          // No editable
    textArea.setBackground(javax.swing.UIManager.getColor("Panel.background"));
    textArea.setCaretPosition(0);
    textArea.setSelectionStart(0);
    textArea.setSelectionEnd(0);
    javax.swing.JScrollPane scroll = new javax.swing.JScrollPane(textArea);
    scroll.setPreferredSize(new java.awt.Dimension(400, 300));
    JOptionPane.showMessageDialog(this, scroll, "Lista de Puntos", JOptionPane.PLAIN_MESSAGE);
}

    private void mostrarAyuda() {
        JOptionPane.showMessageDialog(this, 
            "Proyecto Chafaint v" + APP_VERSION + "\n" +
            "- Click Izquierdo: Poner punto\n" +
            "- Click Derecho: Cerrar figura\n" +
            "- Doble-click: Seleccionar punto\n" +
            "- DELETE: Borrar punto seleccionado\n" +
            "- Arrastrar: Mover punto seleccionado\n" +
            "- Click derecho en punto: Menú contextual\n" +
            "- Usa los menús para Guardar/Abrir");
    }
    

private void SALIR() {
    // --- PRIMER MENÚ: GUARDAR OPCIONAL (solo si hay puntos) ---
    if (!listaPuntos.isEmpty()) {
        int opcionGuardar = JOptionPane.showConfirmDialog(this,
            "¿Desea guardar los cambios antes de salir?",
            "Guardar cambios",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (opcionGuardar == JOptionPane.CANCEL_OPTION) {
            return; // Usuario canceló todo el proceso de salida
        }

        if (opcionGuardar == JOptionPane.YES_OPTION) {
            Boolean guardadoExitoso = guardarArchivo();
            if (guardadoExitoso == null) { // Canceló el guardado
                int salirSinGuardar = JOptionPane.showConfirmDialog(this,
                    "Guardado cancelado.\n¿Salir de todas formas?",
                    "Cancelar",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                if (salirSinGuardar != JOptionPane.YES_OPTION) {
                    return;
                }
            } else if (!guardadoExitoso) { // Error real
                int salirSinGuardar = JOptionPane.showConfirmDialog(this,
                    "No se pudo guardar el archivo.\n¿Salir de todas formas?",
                    "Error al guardar",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                if (salirSinGuardar != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            // Si guardadoExitoso == true, sigue al segundo menú
        }
        // Si eligió NO, continúa al segundo menú
    }

    // --- SEGUNDO MENÚ: CONFIRMAR SALIDA (siempre aparece) ---
    int confirmarSalida = JOptionPane.showConfirmDialog(this,
        "¿Está seguro que desea salir de la aplicación?",
        "Salir",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE);

    if (confirmarSalida == JOptionPane.YES_OPTION) {
        this.dispose();
        System.exit(0);
    }
}
    
    private void AcercaDE() {                                        
        JOptionPane.showMessageDialog(this, 
            "Proyecto Chafaint\n" +
            "Versión: " + APP_VERSION + "\n" +
            "Desarrollado por: " + APP_AUTHOR + "\n");
    }
    
    // Método para encontrar el punto más cercano a las coordenadas
    private int encontrarPuntoCercano(int x, int y, int radio) {
        for (int i = 0; i < listaPuntos.size(); i++) {
            Nodo punto = listaPuntos.get(i);
            double distancia = Math.sqrt(Math.pow(x - punto.x, 2) + Math.pow(y - punto.y, 2));
            if (distancia <= radio) {
                return i;
            }
        }
        return -1;
    }

    // Método para borrar punto seleccionado
    private void borrarPuntoSeleccionado() {
        if (puntoSeleccionado != -1 && !listaPuntos.isEmpty()) {
            // Confirmar borrado
            int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Borrar punto " + (puntoSeleccionado + 1) + "?", 
                "Borrar Punto", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                listaPuntos.remove(puntoSeleccionado);
                puntoSeleccionado = -1; // Deseleccionar
                
                // Si borramos el último punto, ya no está cerrado
                if (listaPuntos.size() < 3) {
                    poligonoCerrado = false;
                }
                
                // Actualizar interfaz
                jLabelElements.setText("Elem: " + listaPuntos.size());
                
                // Cambiar a Modificación si estaba en Archivo
                if (Mode.getText().equals("Modo: Archivo")) {
                    Mode.setText("Modo: Modificación");
                }
                
                lienzo.repaint();
            }
        } else {
            JOptionPane.showMessageDialog(this, "No hay punto seleccionado para borrar.");
        }
    }

    
    
    // Método para seleccionar punto con doble-click
    private void seleccionarPunto(int x, int y) {
        int puntoIndex = encontrarPuntoCercano(x, y, 10); // Radio de 10 píxeles
        
        if (puntoIndex != -1) {
            puntoSeleccionado = puntoIndex;
            lienzo.repaint();
            
            // Mostrar información del punto
            Nodo punto = listaPuntos.get(puntoIndex);
            String info = String.format("Punto %d seleccionado:\nX: %d\nY: %d\nColor: RGB(%d,%d,%d)\nGrosor: %d",
                puntoIndex + 1, punto.x, punto.y, 
                punto.color.getRed(), punto.color.getGreen(), punto.color.getBlue(),
                punto.grosor);
            JOptionPane.showMessageDialog(this, info, "Punto Seleccionado", JOptionPane.INFORMATION_MESSAGE);
        } else {
            puntoSeleccionado = -1;
            lienzo.repaint();
        }
    }

    
    
    // Historial de Versiones asi bien macabron :V
    private void HV() {
        JOptionPane.showMessageDialog(this, 
            "Proyecto Chafaint " + APP_VERSION + "\n" +
            "0.1: Creacion del proyecto,  solo se podia dibujar\n" +
            "0.2: Pruebas de diferentes formas de hacer lineas\n" +
            "0.3: Se pueden hacer figuras a travez de poner puntos\n" +
            "0.3.1: Se corrigio un error pndejo :V\n" +
            "0.3.2: Ahora las ToolBar ya no se salen si se arrastran (y se agregaron mas errores pa no quedarme sin chamba)\n" +
            "0.3.3: Ya no se puede escribir en la tabla de valores y ahora hay un menu de confirmacion al cerrar la aplicacion por si se quiere guardar el trabajo hecho\n" +
            "0.3.4: Se mejoro la fiabilidad de todo este pedo (tiene bugs sin arreglar pa no perder mi chamba)"
);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        jButtonOpen = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jButtonSave = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jButtonColor = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        jButtonValor = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        jButtonClear = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        jButtonHelp = new javax.swing.JButton();
        jToolBar2 = new javax.swing.JToolBar();
        jLabelElements = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        Mode = new javax.swing.JLabel();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        jLabelDay = new javax.swing.JLabel();
        jLabelHour = new javax.swing.JLabel();
        jSeparator10 = new javax.swing.JToolBar.Separator();
        jLabelVersion = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuOpen = new javax.swing.JMenuItem();
        jMenuSave = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuExit = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuSize = new javax.swing.JMenuItem();
        jMenuColor = new javax.swing.JMenuItem();
        jMenuValor = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuClear = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuHelp = new javax.swing.JMenuItem();
        jMenuAD = new javax.swing.JMenuItem();
        jMenuHV = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Chafaint Premium Delux Super Papu Pro Edition");
        setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        setPreferredSize(new java.awt.Dimension(600, 400));

        jToolBar1.setBackground(new java.awt.Color(255, 204, 204));
        jToolBar1.setForeground(new java.awt.Color(255, 204, 204));
        jToolBar1.setRollover(true);

        jButtonOpen.setText("Abrir");
        jButtonOpen.setFocusable(false);
        jButtonOpen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonOpen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonOpen.addActionListener(this::jButtonOpenActionPerformed);
        jToolBar1.add(jButtonOpen);
        jToolBar1.add(jSeparator3);

        jButtonSave.setText("Guardar");
        jButtonSave.setFocusable(false);
        jButtonSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSave.addActionListener(this::jButtonSaveActionPerformed);
        jToolBar1.add(jButtonSave);
        jToolBar1.add(jSeparator4);

        jButtonColor.setText("Color");
        jButtonColor.setFocusable(false);
        jButtonColor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonColor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonColor.addActionListener(this::jButtonColorActionPerformed);
        jToolBar1.add(jButtonColor);
        jToolBar1.add(jSeparator5);

        jButtonValor.setText("Valores");
        jButtonValor.setFocusable(false);
        jButtonValor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonValor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonValor.addActionListener(this::jButtonValorActionPerformed);
        jToolBar1.add(jButtonValor);
        jToolBar1.add(jSeparator6);

        jButtonClear.setText("Limpiar Pantalla");
        jButtonClear.setFocusable(false);
        jButtonClear.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonClear.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonClear.addActionListener(this::jButtonClearActionPerformed);
        jToolBar1.add(jButtonClear);
        jToolBar1.add(jSeparator7);

        jButtonHelp.setText("?");
        jButtonHelp.setFocusable(false);
        jButtonHelp.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonHelp.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonHelp.addActionListener(this::jButtonHelpActionPerformed);
        jToolBar1.add(jButtonHelp);

        jToolBar2.setBackground(new java.awt.Color(255, 204, 204));
        jToolBar2.setForeground(new java.awt.Color(255, 204, 204));
        jToolBar2.setRollover(true);

        jLabelElements.setText("Elementos: ");
        jToolBar2.add(jLabelElements);
        jToolBar2.add(jSeparator8);

        Mode.setText("Modo: ");
        jToolBar2.add(Mode);
        jToolBar2.add(jSeparator9);

        jLabelDay.setText("Dia: ");
        jToolBar2.add(jLabelDay);

        jLabelHour.setText("Hora:");
        jToolBar2.add(jLabelHour);
        jToolBar2.add(jSeparator10);

        jLabelVersion.setText("Version: ");
        jToolBar2.add(jLabelVersion);

        jMenu1.setText("Archivo");

        jMenuOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuOpen.setText("Abrir");
        jMenuOpen.addActionListener(this::jMenuOpenActionPerformed);
        jMenu1.add(jMenuOpen);

        jMenuSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuSave.setText("Guardar");
        jMenuSave.addActionListener(this::jMenuSaveActionPerformed);
        jMenu1.add(jMenuSave);
        jMenu1.add(jSeparator1);

        jMenuExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuExit.setText("Salir");
        jMenuExit.addActionListener(this::jMenuExitActionPerformed);
        jMenu1.add(jMenuExit);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Opciones");

        jMenuSize.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuSize.setText("Grosor de Linea");
        jMenuSize.addActionListener(this::jMenuSizeActionPerformed);
        jMenu2.add(jMenuSize);

        jMenuColor.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuColor.setText("Color");
        jMenuColor.addActionListener(this::jMenuColorActionPerformed);
        jMenu2.add(jMenuColor);

        jMenuValor.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuValor.setText("Valores");
        jMenuValor.addActionListener(this::jMenuValorActionPerformed);
        jMenu2.add(jMenuValor);
        jMenu2.add(jSeparator2);

        jMenuClear.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuClear.setText("Limpiar Pantalla");
        jMenuClear.addActionListener(this::jMenuClearActionPerformed);
        jMenu2.add(jMenuClear);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Ayuda");

        jMenuHelp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuHelp.setText("?");
        jMenuHelp.addActionListener(this::jMenuHelpActionPerformed);
        jMenu3.add(jMenuHelp);

        jMenuAD.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F12, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuAD.setText("Acerca De");
        jMenuAD.addActionListener(this::jMenuADActionPerformed);
        jMenu3.add(jMenuAD);

        jMenuHV.setText("Historial de Versiones");
        jMenuHV.addActionListener(this::jMenuHVActionPerformed);
        jMenu3.add(jMenuHV);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 353, Short.MAX_VALUE)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuOpenActionPerformed
        abrirArchivo();
    }//GEN-LAST:event_jMenuOpenActionPerformed

    private void jMenuSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuSaveActionPerformed
        guardarArchivo();
    }//GEN-LAST:event_jMenuSaveActionPerformed

    private void jMenuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuExitActionPerformed
        SALIR();
    }//GEN-LAST:event_jMenuExitActionPerformed

    private void jMenuSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuSizeActionPerformed
        SizeSelector(); 
    }//GEN-LAST:event_jMenuSizeActionPerformed

    private void jMenuColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuColorActionPerformed
        ColorSelector();
    }//GEN-LAST:event_jMenuColorActionPerformed

    private void jMenuValorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuValorActionPerformed
        mostrarValores();
    }//GEN-LAST:event_jMenuValorActionPerformed

    private void jMenuClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuClearActionPerformed
        limpiarLienzo();
    }//GEN-LAST:event_jMenuClearActionPerformed

    private void jMenuHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuHelpActionPerformed
        mostrarAyuda();
    }//GEN-LAST:event_jMenuHelpActionPerformed

    private void jMenuADActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuADActionPerformed
        AcercaDE();
    }//GEN-LAST:event_jMenuADActionPerformed

    private void jButtonOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOpenActionPerformed
        abrirArchivo();
    }//GEN-LAST:event_jButtonOpenActionPerformed

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        guardarArchivo();
    }//GEN-LAST:event_jButtonSaveActionPerformed

    private void jButtonColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonColorActionPerformed
        ColorSelector();
    }//GEN-LAST:event_jButtonColorActionPerformed

    private void jButtonValorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonValorActionPerformed
        mostrarValores();
    }//GEN-LAST:event_jButtonValorActionPerformed

    private void jButtonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearActionPerformed
        limpiarLienzo();
    }//GEN-LAST:event_jButtonClearActionPerformed

    private void jButtonHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonHelpActionPerformed
        mostrarAyuda();
    }//GEN-LAST:event_jButtonHelpActionPerformed

    private void jMenuHVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuHVActionPerformed
        HV();
    }//GEN-LAST:event_jMenuHVActionPerformed

    /**
     * @param args the command line arguments
     */
public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Main().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Mode;
    private javax.swing.JButton jButtonClear;
    private javax.swing.JButton jButtonColor;
    private javax.swing.JButton jButtonHelp;
    private javax.swing.JButton jButtonOpen;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JButton jButtonValor;
    private javax.swing.JLabel jLabelDay;
    private javax.swing.JLabel jLabelElements;
    private javax.swing.JLabel jLabelHour;
    private javax.swing.JLabel jLabelVersion;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuItem jMenuAD;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuClear;
    private javax.swing.JMenuItem jMenuColor;
    private javax.swing.JMenuItem jMenuExit;
    private javax.swing.JMenuItem jMenuHV;
    private javax.swing.JMenuItem jMenuHelp;
    private javax.swing.JMenuItem jMenuOpen;
    private javax.swing.JMenuItem jMenuSave;
    private javax.swing.JMenuItem jMenuSize;
    private javax.swing.JMenuItem jMenuValor;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    // End of variables declaration//GEN-END:variables

    
    
}
