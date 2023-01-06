package com.example.mama

import android.Manifest
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfDocument
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import com.itextpdf.text.pdf.draw.VerticalPositionMark
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    val file_name: String = "test_pdf.pdf"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val createPDFButton: Button = findViewById(R.id.btn_create_pdf)

        Dexter.withActivity(this)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object: PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    Toast.makeText(this@MainActivity, "Granted", Toast.LENGTH_SHORT).show()
                    createPDFButton.setOnClickListener {
                        Log.d("Ugorji Path",Common.getAppPath(this@MainActivity)+file_name )
                        createPDFFile(Common.getAppPath(this@MainActivity)+file_name)
                    }
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {

                }

            })
            .check()
    }

    private fun createPDFFile(path: String) {
        if (File(path).exists()) {
//            File(path).delete()
        }
        Log.d("Ugorji Path", File(path).toString())

        try {
            val document = Document()
            //Save
            PdfWriter.getInstance(document, FileOutputStream(path))
            //Open to write
            document.open()

            //Setting
            document.pageSize = PageSize.A4
            document.addCreationDate()
            document.addAuthor("Austin")
            document.addCreator("Ugorji")

            //Font Setting
            val colorAccent = BaseColor(0, 153, 204, 255)
            val headingFontSize = 20.0f
            val valueFontSize = 26.0f

            //Custom Font
            val fontName = BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED)

            //Add title to document
            val titleStyle = Font(fontName, 36.0f, Font.NORMAL, BaseColor.BLACK)
            addNewItem(document, "Order Details", Element.ALIGN_CENTER, titleStyle)

            val headingStyle = Font(fontName, headingFontSize, Font.NORMAL, colorAccent)
            addNewItem(document, "Order No:", Element.ALIGN_LEFT, headingStyle)
            val valueStyle = Font(fontName, valueFontSize, Font.NORMAL, BaseColor.BLACK)
            addNewItem(document, "#123123", Element.ALIGN_LEFT, headingStyle)

            addLineSeparator(document)

            addNewItem(document, "Order Date:", Element.ALIGN_LEFT, headingStyle)
            addNewItem(document, "03/08/2019", Element.ALIGN_LEFT, headingStyle)

            addLineSeparator(document)

            addNewItem(document, "Account Name:", Element.ALIGN_LEFT, headingStyle)
            addNewItem(document, "Austin Ugorji", Element.ALIGN_LEFT, headingStyle)

            addLineSeparator(document)


            //Product detail
            addLineSpace(document)
            addNewItem(document, "Product Details", Element.ALIGN_CENTER, titleStyle)

            addLineSeparator(document)

            //Item1
            addNewItemWithLeftAndRight(document, "Pizza 25", "(0.0%)", titleStyle, valueStyle)
            addNewItemWithLeftAndRight(document, "12.0*1000", "12000.0", titleStyle, valueStyle)

            addLineSeparator(document)

            //Item2
            addNewItemWithLeftAndRight(document, "Pizza 26", "(0.0%)", titleStyle, valueStyle)
            addNewItemWithLeftAndRight(document, "12.0*1000", "12000.0", titleStyle, valueStyle)

            addLineSeparator(document)

            //Total
            addLineSpace(document)
            addLineSpace(document)

            addNewItemWithLeftAndRight(document, "Total", "24000.0", titleStyle, valueStyle)

            //close
            document.close()

            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()

            printPDF()


        } catch (e: Exception) {
            Log.e("Ugorjiiii", ""+ e.message)
        }
    }

    private fun printPDF() {
        val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager
        try {
            val printAdapter = PdfDocumentAdapter(this@MainActivity, Common.getAppPath(this@MainActivity)+file_name)
            printManager.print("Document", printAdapter, PrintAttributes.Builder().build())
        }catch (e: Exception) {
            Log.e("Ugorji", "" + e, )
        }
    }


    @Throws(DocumentException::class)
    private fun addNewItemWithLeftAndRight(
        document: Document,
        textLeft: String,
        textRight: String,
        leftStyle: Font,
        rightStyle: Font
    ) {
        val chunkTextLeft = Chunk(textLeft, leftStyle)
        val chunkTextRight = Chunk(textRight, rightStyle)
        val p = Paragraph(chunkTextLeft)
        p.add(Chunk(VerticalPositionMark()))
        p.add(chunkTextRight)
        document.add(p)
    }


    @Throws(DocumentException::class)
    private fun addLineSeparator(document: Document) {
        val lineSeparator = LineSeparator()
        lineSeparator.lineColor = BaseColor(0, 0, 0, 68)
        addLineSpace(document)
        document.add(Chunk(lineSeparator))
        addLineSpace(document)

    }


    @Throws(DocumentException::class)
    private fun addLineSpace(document: Document) {
        document.add(Paragraph(""))
    }

    @Throws(DocumentException::class)
    private fun addNewItem(document: Document, text: String, align: Int, style: Font) {
        val chunk = Chunk(text, style)
        val p = Paragraph(chunk)
        p.alignment = align
        document.add(p)

    }

}

