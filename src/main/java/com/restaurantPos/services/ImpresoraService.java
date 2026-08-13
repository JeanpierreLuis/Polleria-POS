package com.restaurantPos.services;

import org.springframework.stereotype.Service;

import javax.print.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

@Service
public class ImpresoraService {

    private static final String NOMBRE_IMPRESORA = "3nStar";
    private static final Charset TICKET_CHARSET = Charset.forName("Cp850");

    public PrintService buscarImpresora(String nombre) {

        PrintService[] services =
                PrintServiceLookup.lookupPrintServices(null, null);

        for (PrintService service : services) {

            if (service.getName().toLowerCase().contains(nombre.toLowerCase())) {
                return service;
            }
        }

        return null;
    }

    public void imprimirTicket(String ticket) {

        PrintService impresora = buscarImpresora(NOMBRE_IMPRESORA);

        if (impresora == null) {
            System.out.println("No se encontró la impresora.");
            return;
        }
        try {
            byte[] datos = construirDatosTicket(ticket);

            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;

            Doc doc = new SimpleDoc(
                    datos,
                    flavor,
                    null
            );

            DocPrintJob job = impresora.createPrintJob();

            job.print(doc, null);
            System.out.println("Ticket enviado a "+ impresora.getName());
        }catch (PrintException | IOException e){
            throw new RuntimeException(
                    "error al imprimir ticket" + e
            );
        }
    }

    private byte[] construirDatosTicket(String ticket) throws IOException {
        ByteArrayOutputStream datos = new ByteArrayOutputStream();

        datos.write(new byte[]{0x1B, 0x40}); // Inicializar impresora.
        datos.write(new byte[]{0x1B, 0x74, 0x02}); // Tabla de caracteres PC850.
        datos.write(new byte[]{0x1B, 0x4D, 0x00}); // Fuente A: tamano normal de ticket.
        datos.write(ticket.getBytes(TICKET_CHARSET));
        datos.write(new byte[]{0x1D, 0x56, 0x00}); // Corte total.

        return datos.toByteArray();
    }

}
