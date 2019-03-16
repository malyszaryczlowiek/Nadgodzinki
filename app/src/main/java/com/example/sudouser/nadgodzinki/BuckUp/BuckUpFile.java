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
import java.util.List;

import javax.xml.XMLConstants;
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
            rootelement.setAttribute("appName", "Nadgodzinki");
            doc.appendChild(rootelement);

            for (Item i : itemList)
            {
                Element item = doc.createElement("Item");

                Element id = doc.createElement("Id");
                String stringId = Integer.toString(i.getUid());
                Text idText = doc.createTextNode(stringId);
                id.appendChild(idText);
                item.appendChild(id);

                Element dateOfAddition = doc.createElement("DateOfAddition");
                Text dateOfAdditionText = doc.createTextNode(i.getDateOfItemAddition());
                dateOfAddition.appendChild(dateOfAdditionText);
                item.appendChild(dateOfAddition);

                Element dateOfOvertime = doc.createElement("DateOfOvertime");
                Text dateOfOvertimeText = doc.createTextNode(i.getDateOfOvertime());
                dateOfOvertime.appendChild(dateOfOvertimeText);
                item.appendChild(dateOfOvertime);

                Element godzina = doc.createElement("Hours");
                String godzinaString = Integer.toString(i.getNumberOfHours());
                Text hours = doc.createTextNode(godzinaString);
                godzina.appendChild(hours);
                item.appendChild(godzina);

                Element minuty = doc.createElement("Minutes");
                String minutyString = Integer.toString(i.getNumberOfMinutes());
                Text minutes = doc.createTextNode(minutyString);
                minuty.appendChild(minutes);
                item.appendChild(minuty);

                rootelement.appendChild(item);
            }

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
