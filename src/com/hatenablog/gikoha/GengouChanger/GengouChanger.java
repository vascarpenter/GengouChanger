/*
 * Created 2019/04/17
 * Copyright (C) 2019 gikoha
 *
 *         v1.0 initial     電カル上で動くようにJava1.6で記載
 *
 * This file is part of GengouChanger
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.hatenablog.gikoha.GengouChanger;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class GengouChanger extends JFrame
{

    private JPanel panel1;
    private JButton convertButton;
    private JTextArea newtxt;
    private JTextArea oldtxt;

    /**
     * Launch the application.
     */
    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    GengouChanger window = new GengouChanger();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public GengouChanger()
    {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize()
    {
        setTitle("元号変更 1.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panel1);
        pack();
        setBounds(100, 100, 500, 500);
        setVisible(true);

        convertButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                convertButton();
            }
        });


        oldtxt.setRows(6);
        oldtxt.setColumns(40);
        oldtxt.setText(getClipboardString());

        newtxt.setRows(7);
        newtxt.setColumns(40);

    }

    /**
     * 文字列の全置換を行います.
     * @param str - ソース文字列
     * @param target - 検索文字列
     * @param replacement - 置換される文字列
     * @return 置換されたソース文字列
     */
    public String replaceAll(String str, String target, String replacement)
    {
        while (true)
        {
            int beginIndex = str.indexOf(target);
            if (beginIndex == -1) break;
            int endIndex = beginIndex + target.length();
            str = new StringBuffer(str).replace(beginIndex, endIndex,
                    replacement).toString();
        }
        return str;
    }

    /**
     * クリップボードの内容 (TEXT) を返します。
     * @return クリップボードの内容 text
     */
    public static String getClipboardString()
    {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Clipboard clip = kit.getSystemClipboard();
        Transferable contents = clip.getContents(null);
        String result = "";
        boolean hasTransferableText = (contents != null)
                && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if (hasTransferableText)
        {
            try
            {
                result = (String) contents
                        .getTransferData(DataFlavor.stringFlavor);
            }
            catch (UnsupportedFlavorException ex)
            {
                // highly unlikely since we are using a standard DataFlavor
                System.out.println(ex);
                ex.printStackTrace();
            }
            catch (IOException ex)
            {
                System.out.println(ex);
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * システムクリップボードにtextをコピーする
     * @param text - コピーする文字列
     */
    public final void setClipboardString(final String text)
    {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Clipboard clip = kit.getSystemClipboard();

        StringSelection ss = new StringSelection(text);
        clip.setContents(ss, ss);
    }

    /**
     * about boxの表示.
     */
	/*
	public final void aboutBox()
	{
		new AboutBox();
	}
	*/

    /**
     * newTxt内にエラーメッセージを設定する.
     */
    public final void notByouinData()
    {
        newtxt.setText("正しいテキストではない");
    }

    /**
     * フィールドを変換する このアプリケーションの主体.
     */
    public final void convertButton()
    {
        String tx = oldtxt.getText();
        StringTokenizer st = new StringTokenizer(tx, "\n");

        if (!st.hasMoreTokens())
        {
            notByouinData();
            return;
        }

        String t="";
        String tt="";

        while (st.hasMoreTokens())
        {
            t = st.nextToken();
            if (t.trim().equals("")) // 空の行
                continue;
            String t0 =  replaceAll(t.trim(), "\r", "");
            t=t0;
            t0 = "";
            if(t.contains("H"))
            {
                for(;;)
                {
                    int maxlen = t.length();
                    int pos=t.indexOf('H');

                    if(pos<0)   break;
                    if(pos+2>=maxlen)   break;
                    pos++;
                    int lastpos = pos;
                    while(lastpos<maxlen && Character.isDigit(t.charAt(lastpos)))
                    {
                        lastpos++;
                    }
                    if(lastpos == pos)  break;     //  'H'の後が数字じゃない

                    String ty = t.substring(pos,lastpos);
                    int year = Integer.parseInt(ty);
                    if(year>9 && year<34)
                        year = 2019+year-31;
                    else
                    {
                        System.out.printf("invalid: %d\n",year);
                        System.exit(0);
                    }
                    tt += t.substring(0,pos-1) + String.format("%d",year);
                    t = t.substring(lastpos);
                }
                tt += t + "\n";
            }
            else
        		tt += t + "\n";

        }

        newtxt.setText(tt);

        // クリップボードにコピーする
        setClipboardString(tt);
    }

}
