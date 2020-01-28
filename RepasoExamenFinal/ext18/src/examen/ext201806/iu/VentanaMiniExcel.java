package examen.ext201806.iu;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.Connection;
import java.sql.Statement;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.event.*;
import examen.ext201806.datos.*;

public class VentanaMiniExcel extends JFrame {
	private TablaDatos datos;  // Atributo principal: datos de la tabla bidimensional que se muestra
	private JTableExcel tabla; // JTable en la que se muestran los datos
	JScrollPane scrollPane; // JScrollPane para la JTable 
	private static final long serialVersionUID = -5027485765293445079L;
	JLabel verCelda;
	private Logger logger = Logger.getLogger(VentanaMiniExcel.class.getName());
	
    /** Crea la ventana principal del MiniExcel
     * @param dat	Tabla de datos a mostrar y editar
     */
    @SuppressWarnings("serial")
	public VentanaMiniExcel( TablaDatos dat ) {
    	this.datos = dat;
   
		FileHandler fileHandler;
		try {
			fileHandler = new FileHandler("Info.log.xml");
			logger.addHandler(fileHandler);
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

    	// Crear la tabla y el scrollpane
    	tabla = new JTableExcel( datos );
        scrollPane = new JScrollPane( tabla );

        // Inicialización
        setTitle("miniExcel");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        add( scrollPane, "Center" );
        setSize( 700, 300 );
        
        // Crear e inicializar la botonera
        JPanel botonera = new JPanel();
        botonera.setLayout( new FlowLayout( FlowLayout.LEFT ));
        add(botonera, "North");
        JButton bSelec = new JButton( "Sel" );
        botonera.add( bSelec );
        JButton bGuardar = new JButton( "Save" );
        botonera.add( bGuardar );
        JButton bCargar = new JButton( "Load" );
        botonera.add( bCargar );
        JButton bGuardarBD = new JButton("GuardarBD");
        botonera.add(bGuardarBD);
        verCelda = new JLabel( " " );
        botonera.add( verCelda );
        
        // Configuración adicional de JTable (columna 0 no seleccionable)
        tabla.getColumnModel().setSelectionModel(new DefaultListSelectionModel() {
            private boolean isSelectable(int index0, int index1) {
            	if (index0==0) return false;
                return true;
            }
            @Override
            public void setSelectionInterval(int index0, int index1) {
                if(isSelectable(index0, index1)) {
                    super.setSelectionInterval(index0, index1);
                }
            }
            @Override
            public void addSelectionInterval(int index0, int index1) {
                if(isSelectable(index0, index1)) {
                    super.addSelectionInterval(index0, index1);
                }
            }
        });
        
        // Eventos
        ListSelectionListener escuchador = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int fila = tabla.getSelectedRow();
				int col = tabla.getSelectedColumn();
				if (fila>=0 && col>0) {  // Si la celda es editable
					ValorCelda dato = datos.get( fila, col-1 );
					if (dato == null) 
						verCelda.setText( " " );
					else
						verCelda.setText( dato.getTextoEdicion() );
				}
				
				
				logger.log(Level.INFO, "Fila: " + fila + "Columna: " + col + 
						"Texto editado: " + datos.get(fila, col).getTextoEdicion());
			}
		};
        tabla.getSelectionModel().addListSelectionListener( escuchador );
        tabla.getColumnModel().getSelectionModel().addListSelectionListener( escuchador );

        bSelec.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int col = tabla.getSelectedColumn();
				int fila = tabla.getSelectedRow();
				if (col!=-1 && fila!=-1) {
					RefCelda ref = new RefCelda( fila, col );
					datos.selCelda( ref, !datos.isCeldaSel( ref ) );  // Invierte la selección
					tabla.repaint();
					tabla.requestFocus();
				}
				
				logger.log(Level.INFO, "Fila: " + fila + "Columna: " + col );
			}
		});
        bGuardar.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jFileChooser = new JFileChooser();
				jFileChooser.setDialogTitle( "Elige fichero para guardar estos datos" );
				jFileChooser.setCurrentDirectory( new File( ".") );
				jFileChooser.setSelectedFile(new File("miniexcel.mini"));
				jFileChooser.showSaveDialog( VentanaMiniExcel.this );
				File fSalida = jFileChooser.getSelectedFile();
				if (fSalida!=null) datos.escribeAFichero( fSalida );
				logger.log(Level.INFO, fSalida.getAbsolutePath() );
			}
		} );
        bCargar.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jFileChooser = new JFileChooser();
				jFileChooser.setDialogTitle( "Elige fichero de datos a cargar" );
				jFileChooser.setCurrentDirectory( new File( ".") );
				jFileChooser.showSaveDialog( VentanaMiniExcel.this );
				File fEntrada = jFileChooser.getSelectedFile();
				if (fEntrada!=null && fEntrada.exists()) {
					datos.leeDeFichero( fEntrada );
					tabla.repaint();
				}
				logger.log(Level.INFO, fEntrada.getAbsolutePath() );
			}
		} );
        bGuardarBD.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Guardar tabla excell en la BDD
				// Recorrer objeto datos
				Connection conn = BD.initBD("celdas.bd");
				Statement st = BD.usarCrearTablasBD(conn);
				for (int fila = 0; fila < datos.getFilas(); fila++) {
					for(int columna = 0; columna<datos.getColumnas(); columna++) {
						ValorCelda celda = datos.get(fila, columna);
						celda.getTextoEdicion();
						// La celda tiene texto
						if (celda != null && celda.getTextoEdicion() != null 
								&& celda.getTextoEdicion().length()>0) {
							// Si la celda existe en la BD hacemos update
							if (BD.selectCelda(st, fila, columna) != null) {
								BD.celdaUpdate(st, fila, columna, celda.getTextoEdicion());
							} else {
								// Si la celda no existe en la BD hacemos insert
								BD.celdaInsert(st, fila, columna, celda.getTextoEdicion());
							}
						} else {
							// La celda no tiene texto
							BD.celdaDelete(st, fila, columna);
						}
					}
				}
			}
		});
        
    }
    
    
}
