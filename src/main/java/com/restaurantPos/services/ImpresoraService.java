package com.restaurantPos.services;

import org.springframework.stereotype.Service;

import javax.print.*;

@Service
public class ImpresoraService {

    private static final String NOMBRE_IMPRESORA = "3nStar";

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
            byte[] texto = ticket.getBytes();

            byte[] corte = new byte[]{
                    0x1D, 0x56, 0x00
            };

            byte[] datos = new byte[texto.length + corte.length];

            System.arraycopy(
                    texto,
                    0,
                    datos,
                    0,
                    texto.length
            );

            System.arraycopy(
                    corte,
                    0,
                    datos,
                    texto.length,
                    corte.length
            );

            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;

            Doc doc = new SimpleDoc(
                    datos,
                    flavor,
                    null
            );

            DocPrintJob job = impresora.createPrintJob();

            job.print(doc, null);
            System.out.println("Ticket enviado a "+ impresora.getName());
        }catch (PrintException e){
            throw new RuntimeException(
                    "error al imprimir ticket" + e
            );
        }
    }

}
