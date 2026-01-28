/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.univdep;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;

/**
 *
 * @author rescamilla
 */
public class Main extends javax.swing.JFrame {
    private java.awt.Color currentColor = java.awt.Color.BLACK;
    private int currentSize = 10;
    
    
    private static final String APP_VERSION = "0.2"; 
    
    public static String getVersionGlobal() {
        return APP_VERSION;
    }    
    
    
    
    // --- NUEVAS VARIABLES ---
    private java.awt.Point pInicio = null; // Para guardar el primer click de la línea
    private String modoDibujo = "LIBRE";   // Opciones: "LIBRE", "DDA", "BRESENHAM"
    // ------------------------
    private java.awt.image.BufferedImage imagenMemoria;
    private java.awt.Graphics2D g2d;
            
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Main.class.getName());
    /**
     * Creates new form Main
     */
public Main() {
        // 1. INICIALIZAR LA MEMORIA (El Lienzo)
        // Lo creamos antes que nada
        imagenMemoria = new java.awt.image.BufferedImage(2000, 2000, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        g2d = imagenMemoria.createGraphics();
        g2d.setColor(java.awt.Color.WHITE);
        g2d.fillRect(0, 0, 2000, 2000);

        // 2. TRUCO ANTIPARPADEO (Override del ContentPane)
        // Reemplazamos el panel por defecto por uno que dibuje nuestra imagen de fondo
        setContentPane(new javax.swing.JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); // Pinta el fondo base
                if (imagenMemoria != null) {
                    g.drawImage(imagenMemoria, 0, 0, this); // Pinta nuestro dibujo encima
                }
            }
        });

        // 3. CARGAR INTERFAZ (NetBeans)
        // Esto añadirá los menús y barras sobre nuestro panel nuevo
        initComponents();
        
        TxtVersion.setText("Version: " + getVersionGlobal());
        
        
        // 4. CONFIGURAR MENÚS
        jMenuColor.addActionListener(evt -> abrirSelectorColor());
        jMenuSize.addActionListener(evt -> abrirSelectorTamano());
        
        // Configurar Submenú Algoritmos
        javax.swing.JMenu subMenuAlgo = new javax.swing.JMenu("Modo de Dibujo");
        // ... (Tu código de iconos del menú, si lo tienes, va aquí) ...
        subMenuAlgo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/univdep/linea.png")));
        
        javax.swing.ButtonGroup grupoModos = new javax.swing.ButtonGroup();

        javax.swing.JRadioButtonMenuItem rbLibre = new javax.swing.JRadioButtonMenuItem("Pincel Libre");
        rbLibre.setSelected(true);
        rbLibre.addActionListener(e -> { modoDibujo = "LIBRE"; pInicio = null; jTxtColorCode.setText("Modo: Pincel Libre"); });
        grupoModos.add(rbLibre); subMenuAlgo.add(rbLibre);

        javax.swing.JRadioButtonMenuItem rbDDA = new javax.swing.JRadioButtonMenuItem("Línea DDA");
        rbDDA.addActionListener(e -> { modoDibujo = "DDA"; pInicio = null; jTxtColorCode.setText("Modo: DDA"); });
        grupoModos.add(rbDDA); subMenuAlgo.add(rbDDA);

        javax.swing.JRadioButtonMenuItem rbBres = new javax.swing.JRadioButtonMenuItem("Línea Bresenham");
        rbBres.addActionListener(e -> { modoDibujo = "BRESENHAM"; pInicio = null; jTxtColorCode.setText("Modo: Bresenham"); });
        grupoModos.add(rbBres); subMenuAlgo.add(rbBres);

        jMenuTools.add(subMenuAlgo);
        
        // 5. CONFIGURAR EL MOUSE
        javax.swing.event.MouseInputAdapter mouseHandler = new javax.swing.event.MouseInputAdapter() {
            @Override
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                if (modoDibujo.equals("LIBRE")) eventoPintar(evt);
            }
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                if (javax.swing.SwingUtilities.isRightMouseButton(evt)) {
                    eventoPintar(evt); return;
                }
                if (modoDibujo.equals("LIBRE")) {
                    eventoPintar(evt);
                } else {
                    // Lógica de Líneas
                    if (pInicio == null) {
                        pInicio = evt.getPoint();
                        JOptionPane.showMessageDialog(Main.this, "Punto 1. Haz click para el final.");
                    } else {
                        if (modoDibujo.equals("DDA")) dibujarLineaDDA(pInicio, evt.getPoint());
                        else dibujarLineaBresenham(pInicio, evt.getPoint());
                        pInicio = null;
                    }
                }
            }
            @Override
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                actualizarCoordenadas(evt);
            }
        };

        // OJO: Agregamos el listener al getContentPane(), NO a "this"
        // Esto es clave para que las coordenadas sean exactas y no parpadee
        this.getContentPane().addMouseListener(mouseHandler);
        this.getContentPane().addMouseMotionListener(mouseHandler);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        jTxtPositionX = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jTxtPositionY = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jTxtColorCode = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        TxtVersion = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuExit = new javax.swing.JMenuItem();
        jMenuTools = new javax.swing.JMenu();
        jMenuColor = new javax.swing.JMenuItem();
        jMenuSize = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Chafaint");
        setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });

        jToolBar1.setBackground(new java.awt.Color(255, 153, 153));
        jToolBar1.setRollover(true);

        jTxtPositionX.setText("X: 0");
        jToolBar1.add(jTxtPositionX);
        jToolBar1.add(jSeparator1);

        jTxtPositionY.setText("Y: 0");
        jToolBar1.add(jTxtPositionY);
        jToolBar1.add(jSeparator2);

        jTxtColorCode.setText("Color: 0,0,0");
        jToolBar1.add(jTxtColorCode);
        jToolBar1.add(jSeparator3);

        TxtVersion.setBackground(new java.awt.Color(0, 0, 0));
        TxtVersion.setText("Version: ");
        jToolBar1.add(TxtVersion);

        jMenuFile.setText("File");

        jMenuExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/univdep/exit.png"))); // NOI18N
        jMenuExit.setText("Salir");
        jMenuExit.addActionListener(this::jMenuExitActionPerformed);
        jMenuFile.add(jMenuExit);

        jMenuBar1.add(jMenuFile);

        jMenuTools.setText("Tools");

        jMenuColor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/univdep/pngegg.png"))); // NOI18N
        jMenuColor.setText("Color");
        jMenuColor.addActionListener(this::jMenuColorActionPerformed);
        jMenuTools.add(jMenuColor);

        jMenuSize.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/univdep/tamano.png"))); // NOI18N
        jMenuSize.setText("Tamaño");
        jMenuSize.addActionListener(this::jMenuSizeActionPerformed);
        jMenuTools.add(jMenuSize);

        jMenuBar1.add(jMenuTools);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 713, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 494, Short.MAX_VALUE)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        int pos_x = evt.getX();
        int pos_y = evt.getY();

        this.jTxtPositionX.setText("X: " + pos_x);
        this.jTxtPositionY.setText("Y: " + pos_y);
    }//GEN-LAST:event_formMouseMoved

    private void actualizarEtiquetaColor() {
    jTxtColorCode.setText("Color: " + currentColor.getRed() + "," + currentColor.getGreen() + "," + currentColor.getBlue());
    jTxtColorCode.setForeground(currentColor);
}
    
    
    private void jMenuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuExitActionPerformed
 int option = JOptionPane.showConfirmDialog(this,
                                      "Desea salir de la aplicacion?",
                                      "Salir",
                                      JOptionPane.YES_NO_OPTION,
                                      JOptionPane.QUESTION_MESSAGE);
        if(option == JOptionPane.YES_OPTION) {
            this.dispose();
        }
    }//GEN-LAST:event_jMenuExitActionPerformed

    private void jMenuColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuColorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuColorActionPerformed

    private void jMenuSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuSizeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuSizeActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Main().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel TxtVersion;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuColor;
    private javax.swing.JMenuItem jMenuExit;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenuItem jMenuSize;
    private javax.swing.JMenu jMenuTools;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel jTxtColorCode;
    private javax.swing.JLabel jTxtPositionX;
    private javax.swing.JLabel jTxtPositionY;
    // End of variables declaration//GEN-END:variables

    
    private void actualizarCoordenadas(java.awt.event.MouseEvent evt) {
        this.jTxtPositionX.setText("X: " + evt.getX());
        this.jTxtPositionY.setText("Y: " + evt.getY());
    }

    // Agregar esta variable a las declaraciones de clase
private boolean pintando = true; // Estado de pintura

// Reemplazar el método eventoPintar con este:
private void eventoPintar(java.awt.event.MouseEvent evt) {
        actualizarCoordenadas(evt);
        
        if (javax.swing.SwingUtilities.isLeftMouseButton(evt)) {
            if (pintando) {
                // Click Izquierdo: PINTAR EN LA MEMORIA
                g2d.setColor(currentColor);
                // Centramos el óvalo restando la mitad del tamaño
                g2d.fillOval(evt.getX() - currentSize/2, evt.getY() - currentSize/2, currentSize, currentSize);
                
                // Actualizamos la pantalla
                repaint();
            }
        }
        else if (javax.swing.SwingUtilities.isRightMouseButton(evt)) {
            // Click Derecho: ALTERNAR MODO
            pintando = !pintando;
            
            if (pintando) {
                jTxtColorCode.setText("MODO: PINTAR");
            } else {
                jTxtColorCode.setText("MODO: PAUSA (Click derecho para activar)");
            }
        }
    }

    private void abrirSelectorColor() {
        java.awt.Color c = javax.swing.JColorChooser.showDialog(this, "Seleccionar Color", currentColor);
        if (c != null) {
            currentColor = c;
            jTxtColorCode.setText("Color: " + c.getRed() + "," + c.getGreen() + "," + c.getBlue());
            jTxtColorCode.setForeground(c);
        }
    }

    private void abrirSelectorTamano() {
        javax.swing.JSlider slider = new javax.swing.JSlider(1, 50, currentSize);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(2);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        int resultado = JOptionPane.showConfirmDialog(this, slider, "Seleccionar Grosor", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (resultado == JOptionPane.OK_OPTION) {
            currentSize = slider.getValue();
        }
    }
    
    // --- IMPLEMENTACIÓN DE ALGORITMOS ---

    // 1. DDA (Basado en Recta.c)
 // 1. DDA CORREGIDO (Usa g2d y repaint)
    private void dibujarLineaDDA(java.awt.Point p1, java.awt.Point p2) {
        g2d.setColor(currentColor);

        int dx = p2.x - p1.x;
        int dy = p2.y - p1.y;
        
        int pasos = Math.max(Math.abs(dx), Math.abs(dy));
        
        float xInc = (float) dx / pasos;
        float yInc = (float) dy / pasos;
        
        float x = p1.x;
        float y = p1.y;
        
        for (int i = 0; i <= pasos; i++) {
            // Dibujamos en la memoria
            g2d.fillOval(Math.round(x), Math.round(y), currentSize, currentSize);
            x += xInc;
            y += yInc;
        }
        
        // Actualizamos la pantalla al terminar
        repaint();
    }

    // 2. Bresenham (Basado en tu pseudocódigo)
// 2. Bresenham CORREGIDO (Usa g2d y repaint)
    private void dibujarLineaBresenham(java.awt.Point p1, java.awt.Point p2) {
        g2d.setColor(currentColor);
        
        int x0 = p1.x;
        int y0 = p1.y;
        int x1 = p2.x;
        int y1 = p2.y;

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        
        int err = dx - dy;
        
        while (true) {
            // Dibujamos en la memoria
            g2d.fillOval(x0, y0, currentSize, currentSize);
            
            if (x0 == x1 && y0 == y1) break;
            
            int e2 = 2 * err;
            
            if (e2 > -dy) {
                err = err - dy;
                x0 = x0 + sx;
            }
            if (e2 < dx) {
                err = err + dx;
                y0 = y0 + sy;
            }
        }
        
        // Actualizamos la pantalla
        repaint();
    }

    
    
}