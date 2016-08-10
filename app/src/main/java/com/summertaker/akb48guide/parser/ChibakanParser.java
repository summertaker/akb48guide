package com.summertaker.akb48guide.parser;

import android.util.Log;

import com.summertaker.akb48guide.data.GroupData;
import com.summertaker.akb48guide.data.WebData;
import com.summertaker.akb48guide.util.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

public class ChibakanParser extends BaseParser {

    public void parseIndex(String response, GroupData groupData, ArrayList<WebData> dataList) {
        /*
        <div id="sps-itemCategoryBox">
            <ul class="sps-itemList">
                <li class="sps-itemCategoryGroup">
                    <ul>
                        <li class="sps-itemCategoryMidashi"><h3 class="title1">AKB48</h3></li>
                        <li class="sps-itemCategoryList">
                            <ul class="sps-itemCategoryMain">
                                <li class="ca1"><a href="/SHOP/82165/list.html">AKB48公式生写真　あ行</a></li>
                            </ul>
                            <ul class="sps-itemCategorySub">
                                <li class="ca2"><a href="/SHOP/82165/111168/list.html">相笠萌</a></li>
                                ...
        */

        response = clean(response);
        response = Util.getEucjpToUtf8(response);
        //Log.e(mTag, response.substring(0, 300));

        Document doc = Jsoup.parse(response);
        Element root = doc.select(".sps-itemList").first();
        if (root == null) {
            return;
        }

        Element group = null;
        for (Element li : root.select(".sps-itemCategoryGroup")) {
            Element el = li.select("h3").first();
            String name = el.text();
            //Log.e(mTag, name);
            if (name.equals(groupData.getName())) {
                //Log.e(mTag, name);
                group = li;
                break;
            }
        }

        if (group == null) {
            return;
        }

        Element ul = group.select("ul").first();

        for (Element li : ul.select("ul")) {

            if (li.attr("class").equals("sps-itemCategorySub")) {

                for (Element row : li.select("li")) {
                    String name;
                    String url;

                    Element a = row.select("a").first();

                    url = "http://recyclekan.ja.shopserve.jp" + a.attr("href");

                    name = a.text();
                    if (name.contains("メンバー")) {
                        continue;
                    }
                    //Log.e(mTag, name);

                    WebData data = new WebData();
                    data.setName(name);
                    data.setUrl(url);
                    dataList.add(data);
                }
            }
        }
    }

    public String parseList(String response, ArrayList<WebData> dataList) {
        /*
        <div class="layout1">
            <table class="auto" border="0" cellspacing="0" cellpadding="0" width="145px">
                <tr>
                    <td width="145px">
                        <div class="item">
                            <a href="/SHOP/ck144825.html">
                                <img src="http://image1.shopserve.jp/recyclekan.ja.shopserve.jp/pic-labo/simg/akb_20160713_076.jpg?t=20160713131944" alt="AKB48グループ　夏祭り　イベント　2016.7.10　幕張メッセ　会場限定生写真　入山杏奈">
                            </a>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td width="145px">
                        <h2 class="goods">
                            <a href="/SHOP/ck144825.html">AKB48グループ　夏祭り　イベント　2016.7.10　幕張メッセ　会場限定生写真　入山杏奈</a><br>
                            <img src="/hpgen/HPB/theme/img/icon_new.gif" alt="">
                        </h2>
                        <div class="price">
                            1,200円(税抜 1,111円、税 89円)
                        </div>
                        在庫 切れ
                    </td>
                </tr>
            </table>
        </div>
        */

        String totalPage = "";

        response = clean(response);
        response = Util.getEucjpToUtf8(response);
        //Log.e(mTag, response.substring(0, 100));

        Document doc = Jsoup.parse(response);

        for (Element row : doc.select(".layout1")) {
            //Log.e(mTag, el.text());

            String name;
            String url;
            String imageUrl;

            Element a = row.select("a").first();
            url = "http://recyclekan.ja.shopserve.jp" + a.attr("href");

            // http://image1.shopserve.jp/recyclekan.ja.shopserve.jp/pic-labo/simg/akb_20160524_019.jpg?t=20160524133604
            // http://image1.shopserve.jp/recyclekan.ja.shopserve.jp/pic-labo/limg/akb_20160524_019.jpg?t=20160524133604
            // http://image1.shopserve.jp/recyclekan.ja.shopserve.jp/pic-labo/akb_20160524_019.jpg?t=20160524133604
            Element img = a.select("img").first();
            imageUrl = img.attr("src");
            imageUrl = imageUrl.replace("/simg/", "/");
            //Log.e(mTag, imageUrl);

            name = img.attr("alt");

            WebData data = new WebData();
            data.setName(name);
            data.setUrl(url);
            data.setImageUrl(imageUrl);
            dataList.add(data);
        }

        /*
        <p style="width:100%">
            1件～40件&nbsp;（全562件）
            &nbsp;1/15ページ<br />
            <strong>1</strong>
            <a href="javascript:void(0);" onClick="changePage(document.ITEMLIST, 2);return false;">2</a>
            <a href="javascript:void(0);" onClick="changePage(document.ITEMLIST, 3);return false;">3</a>
            <a href="javascript:void(0);" onClick="changePage(document.ITEMLIST, 4);return false;">4</a>
            <a href="javascript:void(0);" onClick="changePage(document.ITEMLIST, 5);return false;">5</a>
            <a href="javascript:void(0);" onClick="changePage(document.ITEMLIST, 2);return false;">次へ</a>
            &nbsp;<a href="javascript:void(0);" onClick="changePage(document.ITEMLIST, 6);return false;">次の5ページへ</a>
            &nbsp;<a href="javascript:void(0);" onClick="changePage(document.ITEMLIST, 15);return false;">最後へ</a>
        </p>
         */

        // http://recyclekan.ja.shopserve.jp/SHOP/82165/95408/list.html
        // http://recyclekan.ja.shopserve.jp/SHOP/82165/95408/t01/list2.html
        for (Element p : doc.select("p")) {

            for (Element a : p.select("a")) {

                if (a.text().equals("最後へ")) {
                    totalPage = a.attr("onClick");
                    totalPage = totalPage.replace("changePage(document.ITEMLIST, ", "");
                    totalPage = totalPage.replace(");return false;", "");
                }
            }
        }

        return totalPage;
    }

    public void parseDetail(String response, ArrayList<WebData> dataList) {
        /*
        <div id="detailphotobloc">
            <div class="photo image_wrap">
				<div class="soldout">
					<img src="//dwd7slh0nmufg.cloudfront.net/user_data/packages/default/img/icon/soldout_500.png">
				</div>
    		    <p><img src="//dwd7slh0nmufg.cloudfront.net/img/products/AK-003-1607-23237_p01_500.jpg?1387605008" width="500" height="500" alt="AKB48 2016年7月度 net shop限定個別生写真 「10th アニバーサリー」衣装 5枚セット 入山杏奈" id="MainPhoto" /></p>
                <ul class="thum_box mb10 mt10">
                    <li class="thum_photo">
                        <img src="//dwd7slh0nmufg.cloudfront.net/img/products/AK-003-1607-23237_p01_500.jpg?1387605008" width="90" alt="AKB48 2016年7月度 net shop限定個別生写真 「10th アニバーサリー」衣装 5枚セット 入山杏奈" class="ChangePhoto" onError="this.parentNode.removeChild(this)">
			  	    </li>
			  	    ...
                </ul>
            </div>
        	<!-- /photo -->
	    </div><!-- /detailphotobloc -->
        */

        response = clean(response);
        //Log.e(mTag, response.substring(0, 100));

        Document doc = Jsoup.parse(response);
        Element root = doc.getElementById("detailphotobloc");
        Element ul = root.select(".thum_box").first();

        for (Element row : ul.select("li")) {
            //Log.e(mTag, blog.html());

            String imageUrl;

            Element img = row.select("img").first();
            String src = img.attr("src");
            imageUrl = "http:" + src;

            //Log.e(mTag, imageUrl);

            WebData data = new WebData();
            data.setImageUrl(imageUrl);
            dataList.add(data);
        }
    }

    private WebData getItemList(String title, Element row) {
        String name;
        String nameOrg;
        String url;
        String imageUrl;

        Element el = row.select(".productImage").first();

        Element img = el.select(".title_icon").first();
        imageUrl = img.attr("src");
        imageUrl = "http:" + imageUrl;
        //Log.e(mTag, imageUrl);

        name = img.attr("alt");
        nameOrg = name;
        name = name.replace(title, "");
        name = name.replace("AKB48", "");
        name = name.replace("SKE48", "");
        name = name.replace("NMB48", "");
        name = name.replace("HKT48", "");
        name = name.trim();
        if (name.isEmpty()) {
            name = nameOrg;
        }

        el = row.select("h4").first();
        Element a = el.select("a").first();
        url = a.attr("href");
        url = "http://shopping.akb48-group.com" + url;

        //Log.e(mTag, title + " / " + url + " / " + date);

        WebData data = new WebData();
        data.setName(name);
        data.setContent(nameOrg);
        data.setUrl(url);
        data.setImageUrl(imageUrl);
        //dataList.add(data);

        return data;
    }
}
