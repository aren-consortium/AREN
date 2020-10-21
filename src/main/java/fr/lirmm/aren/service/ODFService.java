package fr.lirmm.aren.service;

import fr.lirmm.aren.model.Comment;
import fr.lirmm.aren.model.Debate;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.element.style.StyleParagraphPropertiesElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeTextElement;
import org.odftoolkit.odfdom.dom.element.style.StyleListLevelPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleMasterPageElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.dom.element.text.TextListItemElement;
import org.odftoolkit.odfdom.dom.element.text.TextListLevelStyleNumberElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.OdfStylePropertySet;
import org.odftoolkit.odfdom.dom.style.props.OdfPageLayoutProperties;
import org.odftoolkit.odfdom.dom.style.props.OdfStyleProperty;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawImage;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStylePageLayout;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextHeading;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextList;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextListStyle;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextSpan;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author florent
 */
@ApplicationScoped
public class ODFService {

    private OdfTextDocument outputDocument;
    private OdfFileDom contentDom;
    private OdfOfficeStyles stylesOfficeStyles;
    private OfficeTextElement officeText;
    private DateTimeFormatter formater = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");

    private OdfStyleProcessor styleProcessor = new OdfStyleProcessor();

    /**
     *
     */
    public ODFService() {
        try {
            this.outputDocument = OdfTextDocument.newTextDocument();
            this.contentDom = outputDocument.getContentDom();
            this.stylesOfficeStyles = outputDocument.getOrCreateDocumentStyles();
            this.officeText = outputDocument.getContentRoot();
        } catch (Exception ex) {
            Logger.getLogger(ODFService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param debate
     * @return
     * @throws Exception
     */
    public File parseDebate(Debate debate) throws Exception {
        Document document = W3CDom.convert(Jsoup.parse(debate.getDocument().getContent()));

        cleanOutDocument();
        setOfficeStyles();

        //Set main heading
        OdfTextHeading heading = new OdfTextHeading(contentDom, "Heading", debate.getDocument().getName());
        OdfTextSpan span = new OdfTextSpan(contentDom, "Text_20_indice", "");
        span.appendChild(new OdfTextSpan(contentDom, "Text_20_grey", " par "));
        span.addContent(debate.getDocument().getAuthor());
        heading.appendChild(span);
        officeText.appendChild(heading);

        TableTableElement table = new TableTableElement(contentDom);
        table.newTableTableColumnElement().setTableNumberColumnsRepeatedAttribute(2);
        TableTableRowElement row = table.newTableTableRowElement();
        officeText.appendChild(table);

        TableTableCellElement cell = new TableTableCellElement(contentDom);
        htmlToOdf(document.getElementsByTagName("body").item(0), cell);
        cell.setProperty(StyleParagraphPropertiesElement.MarginRight, "0.5cm");
        row.appendChild(cell);

        TableTableCellElement commentsList = new TableTableCellElement(contentDom);
        debate.getComments().forEach((Comment comment) -> {
            if (comment.getParent() == null) {
                parseComment(comment, commentsList, 0);
            }
        });
        row.appendChild(commentsList);

        File file = new File("/tmp/aren_export_" + System.currentTimeMillis() + ".odt");
        this.outputDocument.save(file);
        return file;
    }

    /**
     *
     * @param comment
     * @param root
     * @param level
     */
    public void parseComment(Comment comment, Node root, int level) {
        OdfTextSpan span;
        OdfTextParagraph paragraph;
        String color;

        if (comment.getOpinion() == Comment.Opinion.AGAINST) {
            color = "orange";
        } else if (comment.getOpinion() == Comment.Opinion.FOR) {
            color = "blue";
        } else {
            color = "grey";
        }

        paragraph = new OdfTextParagraph(contentDom, "Text_20_border_left_" + color);
        paragraph.setProperty(StyleParagraphPropertiesElement.MarginLeft, level * 0.5 + "cm");
        paragraph.appendChild(new OdfTextSpan(contentDom, "Text_20_" + color,
                comment.getOwner().getFirstName() + " " + comment.getOwner().getLastName() + " - " + comment.getCreated().format(formater)));
        root.appendChild(paragraph);

        Document selection = W3CDom.convert(Jsoup.parse(comment.getSelection()));
        paragraph = new OdfTextParagraph(contentDom, "Text_20_border_left_" + color,
                selection.getElementsByTagName("body").item(0).getTextContent());
        paragraph.setProperty(StyleParagraphPropertiesElement.MarginLeft, level * 0.5 + "cm");
        styleProcessor.setStyle(paragraph).backgroundColor("#dddfe2");
        root.appendChild(paragraph);

        paragraph = new OdfTextParagraph(contentDom, "Text_20_border_left_" + color);
        paragraph.setProperty(StyleParagraphPropertiesElement.MarginLeft, level * 0.5 + "cm");
        span = new OdfTextSpan(contentDom, "Text_20_italic", comment.getReformulation());
        paragraph.appendChild(span);
        root.appendChild(paragraph);

        paragraph = new OdfTextParagraph(contentDom, "Text_20_border_left_" + color, comment.getArgumentation());
        paragraph.setProperty(StyleParagraphPropertiesElement.MarginLeft, level * 0.5 + "cm");
        root.appendChild(paragraph);

        paragraph = new OdfTextParagraph(contentDom);
        styleProcessor.setStyle(paragraph).fontSize("9pt");
        root.appendChild(paragraph);

        comment.getComments().forEach((Comment child) -> {
            parseComment(child, root, level + 1);
        });
    }

    private void htmlToOdf(Node rootHtml, Node rootOdf) {
        if (rootHtml.hasChildNodes()) {
            for (int i = 0; i < rootHtml.getChildNodes().getLength(); i++) {
                Node childHtml = rootHtml.getChildNodes().item(i);
                Node childOdf;
                switch (childHtml.getNodeName()) {
                    case "#text":
                        if (rootOdf instanceof TextListItemElement) {
                            childOdf = new OdfTextParagraph(contentDom, "", childHtml.getTextContent());
                        } else {
                            childOdf = contentDom.createTextNode(childHtml.getTextContent());
                        }
                        break;
                    case "span":
                        childOdf = new OdfTextSpan(contentDom);
                        break;
                    case "h1":
                        childOdf = new OdfTextHeading(contentDom, "Heading_20_1");
                        break;
                    case "h2":
                        childOdf = new OdfTextHeading(contentDom, "Heading_20_2");
                        break;
                    case "h3":
                        childOdf = new OdfTextHeading(contentDom, "Heading_20_3");
                        break;
                    case "h4":
                        childOdf = new OdfTextHeading(contentDom, "Heading_20_4");
                        break;
                    case "b":
                    case "strong":
                        childOdf = new OdfTextSpan(contentDom, "Text_20_bold");
                        break;
                    case "i":
                    case "em":
                        childOdf = new OdfTextSpan(contentDom, "Text_20_italic");
                        break;
                    case "u":
                        childOdf = new OdfTextSpan(contentDom, "Text_20_uderline");
                        break;
                    case "ul":
                        childOdf = new OdfTextList(contentDom);
                        break;
                    case "li":
                        childOdf = new TextListItemElement(contentDom);
                        break;
                    case "img":
                        childOdf = new OdfDrawImage(contentDom);
                        break;
                    default:
                        childOdf = new OdfTextParagraph(contentDom);
                }

                rootOdf.appendChild(childOdf);
                htmlToOdf(childHtml, childOdf);
            }

        }
    }

    private void cleanOutDocument() {
        org.w3c.dom.Node childNode = officeText.getFirstChild();
        while (childNode != null) {
            officeText.removeChild(childNode);
            childNode = officeText.getFirstChild();
        }
    }

    private void setOfficeStyles() {
        // Set landscape layout
        StyleMasterPageElement defaultPage = outputDocument.getOfficeMasterStyles().getMasterPage("Standard");
        String pageLayoutName = defaultPage.getStylePageLayoutNameAttribute();
        OdfStylePageLayout pageLayoutStyle = defaultPage.getAutomaticStyles().getPageLayout(pageLayoutName);
        pageLayoutStyle.setProperty(OdfPageLayoutProperties.PrintOrientation, "landscape");
        pageLayoutStyle.setProperty(OdfPageLayoutProperties.PageHeight, "21cm");
        pageLayoutStyle.setProperty(OdfPageLayoutProperties.PageWidth, "29.7cm");

        // Set default font
        styleProcessor.setStyle(stylesOfficeStyles.getDefaultStyle(OdfStyleFamily.Paragraph))
                .margins("0cm", "0cm", "0.1cm", "0cm")
                .fontFamilly("Arial")
                .fontSize("11pt")
                .textAlign("justify");

        // Main title
        styleProcessor.setStyle(stylesOfficeStyles.getStyle("Heading", OdfStyleFamily.Paragraph))
                .textAlign("center")
                .margins("0.4cm", "0cm", "0.2cm", "0cm")
                .fontWeight("bold")
                .fontSize("18pt")
                .color("#b84000");

        // Title 1
        styleProcessor.setStyle(stylesOfficeStyles.getStyle("Heading_20_1", OdfStyleFamily.Paragraph))
                .margins("0.35cm", "0cm", "0.2cm", "0cm")
                .fontWeight("bold")
                .fontSize("17pt")
                .color("#b84000");

        // Title 2
        styleProcessor.setStyle(stylesOfficeStyles.getStyle("Heading_20_2", OdfStyleFamily.Paragraph))
                .margins("0.3cm", "0cm", "0.2cm", "0cm")
                .fontWeight("bold")
                .fontSize("15pt")
                .color("#b84000");

        // Title 3
        styleProcessor.setStyle(stylesOfficeStyles.getStyle("Heading_20_3", OdfStyleFamily.Paragraph))
                .margins("0.25cm", "0cm", "0.2cm", "0cm")
                .fontWeight("bold")
                .fontSize("13pt");

        // Title 4
        styleProcessor.setStyle(stylesOfficeStyles.getStyle("Heading_20_4", OdfStyleFamily.Paragraph))
                .margins("0.2cm", "0cm", "0.2cm", "0cm")
                .fontWeight("bold")
                .fontSize("11pt")
                .color("#b84000");

        // Bold
        styleProcessor.setStyle(stylesOfficeStyles.newStyle("Text_20_bold", OdfStyleFamily.Text))
                .fontWeight("bold");

        // Italic
        styleProcessor.setStyle(stylesOfficeStyles.newStyle("Text_20_italic", OdfStyleFamily.Text))
                .fontStyle("italic");

        // Underline
        styleProcessor.setStyle(stylesOfficeStyles.newStyle("Text_20_underline", OdfStyleFamily.Text))
                .textUnderline("auto", "solid", "font-color");

        // Orange
        styleProcessor.setStyle(stylesOfficeStyles.newStyle("Text_20_orange", OdfStyleFamily.Text))
                .color("#b84000");

        // Blue
        styleProcessor.setStyle(stylesOfficeStyles.newStyle("Text_20_blue", OdfStyleFamily.Text))
                .color("#4d9999");

        // Grey
        styleProcessor.setStyle(stylesOfficeStyles.newStyle("Text_20_grey", OdfStyleFamily.Text))
                .color("#b2b2b2");

        // Indice
        styleProcessor.setStyle(stylesOfficeStyles.newStyle("Text_20_indice", OdfStyleFamily.Text))
                .textPosition("sub 50%");

        // Comment List
        TextListLevelStyleNumberElement level;
        OdfTextListStyle listStyle = stylesOfficeStyles.getListStyle("Numbering_20_1");
        for (int i = 0; i < 10; i++) {
            level = (TextListLevelStyleNumberElement) listStyle.getLevel(i + 1);
            level.setStyleNumFormatAttribute("");
            level.setStyleNumSuffixAttribute("");
            level.setProperty(StyleListLevelPropertiesElement.SpaceBefore, (i * 0.5) + "cm");
            level.setProperty(StyleListLevelPropertiesElement.MinLabelWidth, 0 + "cm");
        }

        // Border Orange
        styleProcessor.setStyle(stylesOfficeStyles.newStyle("Text_20_border_left_orange", OdfStyleFamily.Paragraph))
                .paddings("0cm", "0cm", "0cm", "0.2cm")
                .setProperty(StyleParagraphPropertiesElement.BorderLeft, "3pt solid #b84000");

        // Border Blue
        styleProcessor.setStyle(stylesOfficeStyles.newStyle("Text_20_border_left_blue", OdfStyleFamily.Paragraph))
                .paddings("0cm", "0cm", "0cm", "0.2cm")
                .setProperty(StyleParagraphPropertiesElement.BorderLeft, "3pt solid #4d9999");

        // Border Grey
        styleProcessor.setStyle(stylesOfficeStyles.newStyle("Text_20_border_left_grey", OdfStyleFamily.Paragraph))
                .paddings("0cm", "0cm", "0cm", "0.2cm")
                .setProperty(StyleParagraphPropertiesElement.BorderLeft, "3pt solid #b2b2b2");
    }

    private class OdfStyleProcessor {

        private OdfStylePropertySet style;

        public OdfStyleProcessor() {
        }

        public OdfStyleProcessor setStyle(OdfStylePropertySet style) {
            this.style = style;
            return this;
        }

        public OdfStyleProcessor fontFamilly(String value) {
            this.style.setProperty(StyleTextPropertiesElement.FontFamily, value);
            this.style.setProperty(StyleTextPropertiesElement.FontName, value);
            return this;
        }

        public OdfStyleProcessor fontWeight(String value) {
            this.style.setProperty(StyleTextPropertiesElement.FontWeight, value);
            this.style.setProperty(StyleTextPropertiesElement.FontWeightAsian, value);
            this.style.setProperty(StyleTextPropertiesElement.FontWeightComplex, value);
            return this;
        }

        public OdfStyleProcessor fontStyle(String value) {
            this.style.setProperty(StyleTextPropertiesElement.FontStyle, value);
            this.style.setProperty(StyleTextPropertiesElement.FontStyleAsian, value);
            this.style.setProperty(StyleTextPropertiesElement.FontStyleComplex, value);
            return this;
        }

        public OdfStyleProcessor fontSize(String value) {
            this.style.setProperty(StyleTextPropertiesElement.FontSize, value);
            this.style.setProperty(StyleTextPropertiesElement.FontSizeAsian, value);
            this.style.setProperty(StyleTextPropertiesElement.FontSizeComplex, value);
            return this;
        }

        public OdfStyleProcessor textUnderline(String width, String style, String color) {
            this.style.setProperty(StyleTextPropertiesElement.TextUnderlineWidth, width);
            this.style.setProperty(StyleTextPropertiesElement.TextUnderlineStyle, style);
            this.style.setProperty(StyleTextPropertiesElement.TextUnderlineColor, color);
            return this;
        }

        public OdfStyleProcessor margins(String top, String right, String bottom, String left) {
            this.style.setProperty(StyleParagraphPropertiesElement.MarginTop, top);
            this.style.setProperty(StyleParagraphPropertiesElement.MarginRight, right);
            this.style.setProperty(StyleParagraphPropertiesElement.MarginBottom, bottom);
            this.style.setProperty(StyleParagraphPropertiesElement.MarginLeft, left);
            return this;
        }

        public OdfStyleProcessor paddings(String top, String right, String bottom, String left) {
            this.style.setProperty(StyleParagraphPropertiesElement.PaddingTop, top);
            this.style.setProperty(StyleParagraphPropertiesElement.PaddingRight, right);
            this.style.setProperty(StyleParagraphPropertiesElement.PaddingBottom, bottom);
            this.style.setProperty(StyleParagraphPropertiesElement.PaddingLeft, left);
            return this;
        }

        public OdfStyleProcessor color(String value) {
            this.style.setProperty(StyleTextPropertiesElement.Color, value);
            return this;
        }

        public OdfStyleProcessor backgroundColor(String value) {
            this.style.setProperty(StyleParagraphPropertiesElement.BackgroundColor, value);
            return this;
        }

        public OdfStyleProcessor textAlign(String value) {
            this.style.setProperty(StyleParagraphPropertiesElement.TextAlign, value);
            return this;
        }

        public OdfStyleProcessor textPosition(String value) {
            this.style.setProperty(StyleTextPropertiesElement.TextPosition, value);
            return this;
        }

        public OdfStyleProcessor setProperty(OdfStyleProperty prop, String value) {
            this.style.setProperty(prop, value);
            return this;
        }
    }

}
