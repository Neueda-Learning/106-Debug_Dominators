package com.paymentprocessing.payment_processing_system.util;

import com.paymentprocessing.payment_processing_system.dto.StatementResponse;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;


@Component
public class PdfGenerator {


    public byte[] generateStatement(
            StatementResponse statement) {


        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();


        Document document =
                new Document();


        try {

            PdfWriter.getInstance(
                    document,
                    outputStream
            );


            document.open();


            document.add(
                    new Paragraph(
                            "Payment Processing System"
                    )
            );


            document.add(
                    new Paragraph(
                            "Transaction Statement"
                    )
            );


            document.add(
                    new Paragraph(
                            "--------------------------------"
                    )
            );


            document.add(
                    new Paragraph(
                            "Payment ID: "
                                    + statement.getPaymentId()
                    )
            );


            document.add(
                    new Paragraph(
                            "Reference Number: "
                                    + statement.getReferenceNumber()
                    )
            );


            document.add(
                    new Paragraph(
                            "Amount: "
                                    + statement.getAmount()
                    )
            );


            document.add(
                    new Paragraph(
                            "Currency: "
                                    + statement.getCurrency()
                    )
            );


            document.add(
                    new Paragraph(
                            "Status: "
                                    + statement.getStatus()
                    )
            );


            document.add(
                    new Paragraph(
                            "Transaction Date: "
                                    + statement.getTransactionDate()
                    )
            );


            document.add(
                    new Paragraph(
                            "Description: "
                                    + statement.getDescription()
                    )
            );


            document.close();


        } catch (Exception e) {

            throw new RuntimeException(
                    "Error generating payment statement",
                    e
            );
        }


        return outputStream.toByteArray();
    }
}