package com.example.sudouser.nadgodzinki.BuckUp;

import android.content.Context;

import com.example.sudouser.nadgodzinki.R;
import com.example.sudouser.nadgodzinki.db.Item;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import androidx.annotation.NonNull;


public class BuckUpFile
{
    private File plik;

    public BuckUpFile(Context context, @NonNull List<Item> itemList)
    {
        plik = null;

        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element rootelement = doc.createElement("ListOfItems");
            doc.appendChild(rootelement);

            for (Item i : itemList)
            {
                Element item = doc.createElement("Item");

                Element data = doc.createElement("Date");
                Text date = doc.createTextNode("date");
                data.appendChild(date);
                item.appendChild(data);

                Element godzina = doc.createElement("Hours");
                Text hours = doc.createTextNode("hours");
                godzina.appendChild(hours);
                item.appendChild(godzina);

                Element minuty = doc.createElement("Minutes");
                Text minutes = doc.createTextNode("minutes");
                minuty.appendChild(minutes);
                item.appendChild(minuty);

                rootelement.appendChild(item);
            }

            LocalDate date = LocalDate.now();
            plik = new File(context.getCacheDir(), context.getText(R.string.app_name) + "_Buckup.xml");
            plik.deleteOnExit();

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");

            StreamResult streamResult = new StreamResult(Files.newOutputStream(plik.toPath()));
            transformer.transform(new DOMSource(doc), streamResult);
        }
        catch (javax.xml.parsers.ParserConfigurationException | javax.xml.transform.TransformerConfigurationException e)
        {
            e.printStackTrace();
        }
        catch (TransformerException | IOException e)
        {
            e.printStackTrace();
        }
    }

    public File getFile()
    {
        return plik;
    }
}
