package com.summertaker.akb48guide.parser;

import com.summertaker.akb48guide.common.Config;
import com.summertaker.akb48guide.data.GroupData;
import com.summertaker.akb48guide.data.MemberData;
import com.summertaker.akb48guide.util.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

public class WikipediaEnParser extends BaseParser {

    public String getUrl(String groupId) {
        String url = "";
        switch (groupId) {
            case Config.GROUP_ID_AKB48:
                url = "https://en.wikipedia.org/wiki/List_of_" + groupId + "_members";
                break;
            case Config.GROUP_ID_NOGIZAKA46:
            case Config.GROUP_ID_KEYAKIZAKA46:
                url = "https://en.wikipedia.org/wiki/" + Util.ucfirst(groupId.toLowerCase());
                break;
            default:
                url = "https://en.wikipedia.org/wiki/" + groupId;
                break;
        }
        return url;
    }

    public void parse48List(String response, GroupData groupData, ArrayList<MemberData> memberList) {
        response = clean(response);
        Document doc = Jsoup.parse(response);

        if (groupData.getId().equals(Config.GROUP_ID_NGT48)) {
            /*
            <table class="wikitable sortable" style="text-align:center;">
            <tr>
                <th rowspan="2">Name</th>
                <th rowspan="2">Birth date (age)</th>
                <th class="unsortable">Election rank</th>
            </tr>
            <tr>
                <th data-sort-type="number">1</th>
            </tr>
            <tr>
                <td style="text-align:left;" data-sort-value="Yuki Kashiwagi">
                    Yuka Ogino
                    ### 또는 (이름은 2가지 형태)
                    <a href="/wiki/Yuki_Kashiwagi" title="Yuki Kashiwagi">Yuki Kashiwagi</a>

                    <span style="font-weight: normal">
                    (
                        <span class="t_nihongo_kanji" lang="ja">荻野由佳</span>
                        <sup class="t_nihongo_help noprint">
                            <a href="/wiki/Help:Installing_Japanese_character_sets" title="Help:Installing Japanese character sets">
                                <span class="t_nihongo_icon" style="color: #00e; font: bold 80% sans-serif; text-decoration: none; padding: 0 .1em;">?</span>
                            </a>
                        </sup>
                    )
                    </span>
                </td>
                <td style="text-align:left;">
                    <span style="display:none">(<span class="bday">1999-02-16</span>)</span>
                    February 16, 1999
                    <span class="noprint ForceAgeToShow">(age&#160;17)</span>
                </td>
                <td></td>
            </tr>
            ...
            */
            for (Element table : doc.select(".wikitable")) {

                Element tr1 = table.select("tr").first();
                Element th1 = tr1.select("th").first();
                if (th1 == null || th1.text() == null) {
                    continue;
                }
                if (!th1.text().equals("Name")) {
                    continue;
                }

                int count = 0;
                for (Element tr : table.select("tr")) {

                    String nameJa;
                    String nameEn;
                    String noSpaceLocalName;

                    count++;
                    if (count < 3) {
                        continue;
                    }

                    Element td = tr.select("td").first();
                    if (td == null) {
                        continue;
                    }

                    String[] array = td.html().split("<span ");
                    if (array.length < 2) {
                        continue;
                    }
                    nameEn = array[0].trim();
                    Document en = Jsoup.parse(nameEn);
                    nameEn = en.text().trim();

                    Element ja = td.select(".t_nihongo_kanji").first();
                    if (ja == null) {
                        continue;
                    }
                    nameJa = ja.text().trim();

                    //Log.e(mTag, nameEn + " / " + nameJa);

                    MemberData memberData = new MemberData();
                    memberData.setNameJa(nameJa);
                    memberData.setNameEn(nameEn);

                    memberList.add(memberData);
                }
            }

            /*
            <h3>
                <span class="mw-headline" id="Trainees">Trainees</span>
                <span class="mw-editsection">
                    <span class="mw-editsection-bracket">[</span>
                    <a href="/w/index.php?title=NGT48&amp;action=edit&amp;section=4" title="Edit section: Trainees">edit</a>
                    <span class="mw-editsection-bracket">]</span>
                </span>
            </h3>
            <ul>
                <li>
                    Yuria Ōtaki
                    <span style="font-weight: normal">
                        (
                            <span class="t_nihongo_kanji" lang="ja">大滝友梨亜</span>
                            <span class="t_nihongo_comma" style="display:none">,</span>
                            <i>Ōtaki Yuria</i>
                            <sup class="t_nihongo_help noprint">
                                <a href="/wiki/Help:Installing_Japanese_character_sets" title="Help:Installing Japanese character sets">
                                    <span class="t_nihongo_icon" style="color: #00e; font: bold 80% sans-serif; text-decoration: none; padding: 0 .1em;">?</span>
                                </a>
                            </sup>,
                            <span style="display:none">
                                (<span class="bday">1995-04-21</span>)
                            </span>
                            April 21, 1995
                            <span class="noprint ForceAgeToShow">(age&#160;20)</span>
                        )
                    </span>
                </li>
                ...
            */
            Element span = doc.getElementById("Trainees");
            if (span == null) {
                return;
            }

            Element h3 = span.parent();
            if (h3 == null) {
                return;
            }
            Element ul = h3.nextElementSibling();
            if (ul == null) {
                return;
            }

            for (Element li : ul.select("li")) {

                String nameJa;
                String nameEn;

                String[] array = li.html().split("<span ");
                if (array.length < 2) {
                    continue;
                }
                nameEn = array[0].trim();

                Element ja = li.select(".t_nihongo_kanji").first();
                if (ja == null) {
                    continue;
                }
                nameJa = ja.text().trim();

                //Log.e(mTag, nameEn + " / " + nameJa);

                MemberData memberData = new MemberData();
                memberData.setNameJa(nameJa);
                memberData.setNameEn(nameEn);

                memberList.add(memberData);
            }
        } else {
            /*
            <table class="wikitable sortable" style="text-align:center; width:100%;">
            <tr>
                <th rowspan="2">Name</th>
                <th rowspan="2">Birth date (age)</th>
                <th colspan="7" class="unsortable">Election rank</th>
            </tr>
            <tr>
                <th data-sort-type="number">1<sup id="cite_ref-vote1_9-0" class="reference"><a href="#cite_note-vote1-9">[9]</a></sup></th>
                <th data-sort-type="number">2<sup id="cite_ref-vote2_10-0" class="reference"><a href="#cite_note-vote2-10">[10]</a></sup></th>
                <th data-sort-type="number">3<sup id="cite_ref-vote3_11-0" class="reference"><a href="#cite_note-vote3-11">[11]</a></sup></th>
                <th data-sort-type="number">4<sup id="cite_ref-vote4_12-0" class="reference"><a href="#cite_note-vote4-12">[12]</a></sup></th>
                <th data-sort-type="number">5<sup id="cite_ref-election2013_13-0" class="reference"><a href="#cite_note-election2013-13">[13]</a></sup></th>
                <th data-sort-type="number">6<sup id="cite_ref-election2014_14-0" class="reference"><a href="#cite_note-election2014-14">[14]</a></sup></th>
                <th data-sort-type="number">7<sup id="cite_ref-election2015_15-0" class="reference"><a href="#cite_note-election2015-15">[15]</a></sup></th>
            </tr>
            <tr>
                <td style="text-align:left;" data-sort-value="Iriyama, Anna">
                    <a href="/wiki/Anna_Iriyama" title="Anna Iriyama">Anna Iriyama</a>
                    <span style="font-weight: normal">
                    (
                        <span class="t_nihongo_kanji" lang="ja">入山 杏奈</span>
                        <span class="t_nihongo_comma" style="display:none">,</span>
                        <i>Iriyama Anna</i>
                    )
                    </span>
                </td>
                <td style="text-align:left;"><span style="display:none">(<span class="bday">1995-12-03</span>)</span> December 3, 1995 <span class="noprint ForceAgeToShow">(age&#160;20)</span></td>
                <td style="background:#bbb;"><span style="display:none" class="sortkey">7002999000000000000♠</span>&#160;</td>
                <td style="background:#ececec; color:gray;"><span style="display:none" class="sortkey">7002888000000000000♠</span><small style="font-size:85%;">N/A</small></td>
                <td style="background:#ececec; color:gray;"><span style="display:none" class="sortkey">7002888000000000000♠</span><small style="font-size:85%;">N/A</small></td>
                <td style="background:#ececec; color:gray;"><span style="display:none" class="sortkey">7002888000000000000♠</span><small style="font-size:85%;">N/A</small></td>
                <td>30</td>
                <td>20</td>
                <td style="background:#bbb;"><span style="display:none" class="sortkey">7002999000000000000♠</span>&#160;</td>
            </tr>
            ...
            */
            for (Element table : doc.select(".wikitable")) {

                Element tr1 = table.select("tr").first();
                Element th1 = tr1.select("th").first();
                if (th1 == null || th1.text() == null) {
                    continue;
                }
                if (!th1.text().equals("Name")) {
                    continue;
                }

                int count = 0;
                for (Element tr : table.select("tr")) {

                    String nameJa;
                    String nameEn;

                    count++;
                    if (count < 2) {
                        continue;
                    }

                    Element td = tr.select("td").first();
                    if (td == null) {
                        continue;
                    }
                    Element a = td.select("a").first();
                    if (a != null) {
                        nameEn = a.text().trim();
                    } else {
                        String[] array = td.html().split("<span ");
                        nameEn = array[0].trim();
                    }

                    Element span = td.select("span").first();
                    if (span == null) {
                        continue;
                    }
                    Element kanji = span.select(".t_nihongo_kanji").first();
                    if (kanji == null) {
                        continue;
                    }
                    nameJa = kanji.text().trim();

                    //Log.e(mTag, nameEn + " / " + nameJa);

                    MemberData memberData = new MemberData();
                    memberData.setNameJa(nameJa);
                    memberData.setNameEn(nameEn);

                    memberList.add(memberData);
                }
            }
        }
    }

    public void parse46List(String response, GroupData groupData, ArrayList<MemberData> memberList) {
        /*
        <table class="wikitable sortable" style="text-align:left; font-size:small;">
        <tr>
            <th>Name</th>
            <th>Birth date (age)</th>
            <th>Native</th>
            <th>Height</th>
            <th>Generation</th>
            <th>Notes</th>
        </tr>
        <tr>
            <td>
                Manatsu Akimoto
                <span style="font-weight: normal">
                (
                    <span class="t_nihongo_kanji" lang="ja">秋元真夏</span>
                    <sup class="t_nihongo_help noprint">
                        <a href="/wiki/Help:Installing_Japanese_character_sets" title="Help:Installing Japanese character sets">
                            <span class="t_nihongo_icon" style="color: #00e; font: bold 80% sans-serif; text-decoration: none; padding: 0 .1em;">?</span>
                        </a>
                    </sup>
                )
               </span>
            </td>
            <td><span style="display:none">(<span class="bday">1993-08-20</span>)</span> August 20, 1993 <span class="noprint ForceAgeToShow">(age&#160;22)</span></td>
            <td>Saitama</td>
            <td>156&#160;cm</td>
            <td>1</td>
            <td></td>
        </tr>
        */
        //Log.e(mTag, response);

        response = clean(response);
        Document doc = Jsoup.parse(response);

        Element el;

        for (Element table : doc.select(".wikitable")) {
            //Log.e(mTag, table.html());

            Element tr1 = table.select("tr").first();
            Element th1 = tr1.select("th").first();
            if (th1 == null || th1.text() == null) {
                continue;
            }
            if (!th1.text().trim().equals("Name")) {
                continue;
            }

            int count = 0;
            for (Element tr : table.select("tr")) {
                //Log.e(mTag, tr.html());

                String nameEn;
                String nameJa;

                count++;
                //Log.e(mTag, "count: " + count);

                if (count < 2) {
                    continue;
                }

                Element td = tr.select("td").first();
                if (td == null) {
                    continue;
                }
                String[] array = td.html().split("<span ");
                if (array.length < 2) {
                    continue;
                }
                nameEn = array[0].trim();

                Element span = td.select("span").first();
                Element kanji = span.select(".t_nihongo_kanji").first();
                if (kanji == null) {
                    continue;
                }
                nameJa = kanji.text().trim();

                //Log.e(mTag, nameEn + "/" + nameJa);

                MemberData memberData = new MemberData();
                memberData.setNameEn(nameEn);
                memberData.setNameJa(nameJa);
                memberList.add(memberData);
            }
        }
    }
}


