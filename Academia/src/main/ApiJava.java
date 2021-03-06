package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ApiJava {

	public static void main(String[] args) {
		// Ejemplo SimpleDateFormat
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		try {
			// Java 7 o menor
			Date fecha = sdf.parse("22/01/2020");
			
			Date fechaAhora = new Date();
			System.out.println("Fecha actual: " + sdf.format(fechaAhora));
			
			// Utiliza a dia de hoy
			LocalDate fech = LocalDate.now();
			LocalDate fechaCustom = LocalDate.of(2020, 1, 1);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Ejemplo Logger
		Logger logger = Logger.getLogger(ApiJava.class.getName());
		logger.log(Level.INFO, "Primer log");
		FileHandler fInfo;
		try {
			fInfo = new FileHandler("Info.log.xml");
			logger.addHandler(fInfo);
			
			logger.log(Level.INFO, "Escribir en fichero" + fInfo);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE, "Error Escribir fichero", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE, "Error Escribir fichero", e);
		}
		
		// Ejemplo properties
		Properties properties = new Properties();
		properties.setProperty("prueba", "prueba");
		try {
			properties.store(new FileOutputStream("properties.xml"), "propiedades");
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		// Leer/Cargar propiedades
		try {
			properties.load(new FileInputStream("properties.xml"));
			String propiedadLeida = (String) properties.get("prueba");
			System.out.println(propiedadLeida);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
