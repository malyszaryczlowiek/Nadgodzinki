package com.example.sudouser.nadgodzinki.BuckUp;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.example.sudouser.nadgodzinki.R;
import com.example.sudouser.nadgodzinki.db.Item;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import androidx.appcompat.app.AlertDialog;

public class XmlParser
{
    private ArrayList<Item> list = null;

    public XmlParser (Context context, File file)
    {
        try
        {
            list = new ArrayList<>();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            Element root = document.getDocumentElement();

            // sprawdzenie czy root element ma odpowiednią nazwe jeśli nie ma to nie jest to właściwy
            // dokument i nie można przeprowadzić dalszego parsingu
            if (root.getAttribute("appName").equals("Nadgodzinki"))
            {
                NodeList listOfItems = root.getChildNodes(); // jesteśmy teraz w roocie i listujemy itemy i whitespace'y
                for (int i = 0; i < listOfItems.getLength(); ++i)
                {
                    Node item = listOfItems.item(i);
                    if (item instanceof Element) // omijamy whitespace przy przechodzeniu pomiędzy itemami
                    { // mamy obiekt <Item>
                        NodeList listOFItemsElements = item.getChildNodes();
                        // musimy wyitemować elementy z każdego pojedyńczego itemu
                        String id = "";
                        String dateOfAddition = "";
                        String dateOfOvertime = "";
                        String hours = "";
                        String minutes = "";
                        // jesteśmy teraz w itemie i iterujemy po kolei każdy node
                        for (int j = 0; j < listOFItemsElements.getLength(); ++j)
                        {
                            // każdy element nodu <item> iterujemy w for
                            Node element = listOFItemsElements.item(j);
                            // ekstrahujemy tutaj przy pomocy instance tylko rzeczywiste nody a wyrzucamy whitespace
                            if (element instanceof Element)
                            {
                                Element cecha = (Element) element;
                                Text textNode = (Text) cecha.getFirstChild();
                                String value = textNode.getData().trim(); // trim() omija whitespaces w wartości elementu
                                switch (cecha.getTagName())
                                {
                                    //TODO jeśli new Item() wyjebie nullPointerException to znaczy, że trzeba wywalić break
                                    // bo on  pewnie powoduje wyjście z pętli
                                    case "Id":
                                        id = value;
                                        break;
                                    case "DateOfAddition":
                                        dateOfAddition = value;
                                        break;
                                    case "DateOfOvertime":
                                        dateOfOvertime = value;
                                        break;
                                    case "Hours":
                                        hours = value;
                                        break;
                                    case "Minutes":
                                        minutes = value;
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                        //   if (dateOfAddition != null && dateOfOvertime != null && hours != null && minutes != null)
                        Item overhour = new Item(Integer.parseInt(id), dateOfAddition, dateOfOvertime, Integer.parseInt(hours), Integer.valueOf(minutes));
                        list.add(overhour);

                        // zrobić tak, żeby uzyskaną listę element po elemencie dodawać do bazy danych sprawdzając czy dany element już w niej jest
                        // jeśli jest to niech nie będzie dodawany jeśli nie ma to dodoać go.
                    }
                }
            }
            else
            {
                // info że cannot parse xml file
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                dialogBuilder.setTitle(R.string.error)
                        .setMessage(R.string.cannot_parse_xml_file)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.cancel();
                            }
                        });
                dialogBuilder.show();
            }
        }
        catch (javax.xml.parsers.ParserConfigurationException e)
        {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        catch (java.io.IOException | org.xml.sax.SAXException e)
        {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public List<Item> returnList()
    {
        return list;
    }
}


/*
pisze DTD który będzie wykorzystany do parsingu pliku XML

<!DOCTYPE ListOfItems [
<!Element ListOfItems (
   <!ELEMENT Item (
       Id, DateOfAddition, DateOfOvertime, Hours, Minutes
   )>
)+> // + mowi o tym, że list of items ma zawierać co najmniej jeden item.
]>


albo inaczej
<!DOCTYPE ListOfItems [
<!Element ListOfItems (Item)+> // + oznacza że Item ma występować co najmniej jeden
<!Element Item ( Id, DateOfAddition, DateOfOvertime, Hours, Minutes )> // zbiór danych w nawiasie występuje tylko raz
<!Element Id (#PCDATA)>
<!Element DateOfAddition (#PCDATA)>
<!Element DataOfOvertime (#PCDATA)>
<!Element Hours (#PCDATA)>
<!Element Minutes (#PCDATA)>

]> // koniec




 */






























