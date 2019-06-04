package com.github.malyszaryczlowiek.nadgodzinki.BuckUp;

import com.github.malyszaryczlowiek.nadgodzinki.db.Item;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

    public BuckUpFile(File directory, @NonNull List<Item> itemList)
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

                Element dayOfWeek = doc.createElement("DayOfWeek");
                String day = Integer.toString(i.getDayOfWeek());
                Text dayOfWeekText = doc.createTextNode(day);
                dayOfWeek.appendChild(dayOfWeekText);
                item.appendChild(dayOfWeek);

                Element yearOfOvertime = doc.createElement("YearOfOvertime");
                String yearOfOvertimeString = Integer.toString(i.getYearOfOvertime());
                Text yearOfOvertimeText = doc.createTextNode(yearOfOvertimeString);
                yearOfOvertime.appendChild(yearOfOvertimeText);
                item.appendChild(yearOfOvertime);

                Element monthOfOvertime = doc.createElement("MonthOfOvertime");
                String monthOfOvertimeString = Integer.toString(i.getMonthOfOvertime());
                Text monthOfOvertimeText = doc.createTextNode(monthOfOvertimeString);
                monthOfOvertime.appendChild(monthOfOvertimeText);
                item.appendChild(monthOfOvertime);

                Element dayOfOvertime = doc.createElement("DayOfOvertime");
                String dayOfOvertimeString = Integer.toString(i.getDayOfOvertime());
                Text dayOfOvertimeText = doc.createTextNode(dayOfOvertimeString);
                dayOfOvertime.appendChild(dayOfOvertimeText);
                item.appendChild(dayOfOvertime);

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

                Element note = doc.createElement("Note");
                String noteToAdd = i.getNote();
                if (noteToAdd.equals(""))
                    noteToAdd = "no_note";
                Text noteText = doc.createTextNode(noteToAdd);
                note.appendChild(noteText);
                item.appendChild(note);

                rootelement.appendChild(item);
            }

            plik = new File(directory, "Nadgodzinki_BuckUp.xml");
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
