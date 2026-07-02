/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.internal.util;

import com.sk89q.worldedit.WorldEditManifest;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public class InfoEntryPoint {
    private static final String INSTALL_URL = "https://worldedit.enginehub.org/en/latest/install/";
    private static final String SUPPORT_URL = "https://discord.gg/enginehub";

    private static String getMessage(boolean html) {
        WorldEditManifest manifest = WorldEditManifest.load();
        return "To install WorldEdit, place it in the " + manifest.getWorldEditKind().folderName + " folder.\nFor more detailed instructions, see " + InfoEntryPoint.formatLink(INSTALL_URL, html) + "\nFor further help, check out our support Discord at " + InfoEntryPoint.formatLink(SUPPORT_URL, html) + "\n\nVersion: " + manifest.getWorldEditVersion() + "\n";
    }

    private static String formatLink(String url, boolean html) {
        return html ? String.format("<a href=\"%1$s\">%1$s</a>", url) : url;
    }

    public static void main(String[] args) {
        if (System.console() != null) {
            System.err.println(InfoEntryPoint.getMessage(false));
        } else {
            System.setProperty("awt.useSystemAAFontSettings", "lcd");
            JOptionPane.showMessageDialog(null, new NavigableEditorPane(InfoEntryPoint.getMessage(true)), "WorldEdit", 1);
        }
        System.exit(1);
    }

    private static class NavigableEditorPane
    extends JTextPane {
        public NavigableEditorPane(String htmlBody) {
            super(new HTMLDocument());
            this.setEditorKit(new HTMLEditorKit());
            this.setText(htmlBody.replace("\n", "<br>"));
            this.setBackground(UIManager.getColor("Panel.background"));
            this.addHyperlinkListener(e -> {
                if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                    try {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    }
                    catch (IOException | URISyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            this.setEditable(false);
            this.setBorder(null);
        }
    }
}

