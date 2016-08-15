package com.summertaker.akb48guide.data;

import android.content.Context;
import android.content.res.Resources;

import com.summertaker.akb48guide.R;
import com.summertaker.akb48guide.common.Config;

import java.util.ArrayList;

public class DataManager {

    private String mTag;
    private Context mContext;
    private Resources mRes;

    //private Context mContext;
    //private SharedPreferences mSharedPreferences;

    public DataManager(Context context) {
        this.mTag = this.getClass().getSimpleName();
        this.mContext = context;
        this.mRes = context.getResources();
        //mSharedPreferences = context.getSharedPreferences(Config.USER_PREFERENCE_KEY, 0);
    }

    /*public JSONObject getBaseData() {
        String jsonString = mSharedPreferences.getString("jsonString", "");
        //Log.e(mTag, "jsonString: " + jsonString);

        JSONObject jsonObject = null;
        if (jsonString != null && !jsonString.isEmpty()) {
            try {
                jsonObject = new JSONObject(jsonString);
            } catch (JSONException e) {
                Log.e(mTag, "JSONException: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    public void setBaseData(String parseDate, String jsonString) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("parseDate", parseDate);
        editor.putString("jsonString", jsonString);
        editor.commit();
    }

    public String getParseDate() {
        return mSharedPreferences.getString("parseDate", "");
    }

    public void clearBaseData() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.commit();
    }*/

    public ArrayList<GroupData> getGroupList(String action) {
        ArrayList<GroupData> dataList = new ArrayList<>();

        dataList.add(new GroupData(Config.GROUP_ID_AKB48,
                "AKB48",
                R.drawable.logo_akb48,
                "http://www.akb48.co.jp/about/members/",
                "http://sp.akb48.co.jp/profile/member/index.php?g_code=all",
                "http://shopping.akb48-group.com/products/list.php?akb48&category_id=3"
        ));
        dataList.add(new GroupData(Config.GROUP_ID_SKE48,
                "SKE48",
                R.drawable.logo_ske48,
                "http://www.ske48.co.jp/profile/list.php",
                null,
                "http://shopping.akb48-group.com/products/list.php?ske48&category_id=126"
        ));
        dataList.add(new GroupData(Config.GROUP_ID_NMB48,
                "NMB48",
                R.drawable.logo_nmb48,
                "http://www.nmb48.com/member/",
                null,
                "http://shopping.akb48-group.com/products/list.php?nmb48&category_id=506"
        ));
        dataList.add(new GroupData(Config.GROUP_ID_HKT48,
                "HKT48",
                R.drawable.logo_hkt48,
                "http://www.hkt48.jp/profile/",
                "http://sp.hkt48.jp/qhkt48_list",
                "http://shopping.akb48-group.com/products/list.php?hkt48&category_id=245"
        ));
        dataList.add(new GroupData(Config.GROUP_ID_NGT48,
                "NGT48",
                R.drawable.logo_ngt48,
                "http://ngt48.jp/profile",
                "https://ngt48.com/profile",
                null
        ));
        dataList.add(new GroupData(Config.GROUP_ID_JKT48,
                "JKT48",
                R.drawable.logo_jkt48,
                "http://www.jkt48.com/member/list?lang=id",
                null,
                null
        ));
        dataList.add(new GroupData(Config.GROUP_ID_SNH48,
                "SNH48",
                R.drawable.logo_snh48,
                "http://www.snh48.com/member_list.php",
                null,
                null
        ));
        if (!action.equals(Config.MAIN_ACTION_BIRTHDAY)) {
            dataList.add(new GroupData(Config.GROUP_ID_BEJ48,
                    "BEJ48",
                    R.drawable.logo_bej48,
                    "http://www.bej48.com/member.html",
                    null,
                    null
            ));
            dataList.add(new GroupData(Config.GROUP_ID_GNZ48,
                    "GNZ48",
                    R.drawable.logo_gnz48,
                    "http://www.gnz48.com/member/member_list.php",
                    null,
                    null
            ));
        }

        /*dataList.add(new GroupData(Config.GROUP_ID_NOGIZAKA46,
                mRes.getString(R.string.nogizaka46),
                R.drawable.logo_nogizaka46,
                "http://www.nogizaka46.com/member/",
                "http://www.nogizaka46.com/smph/member/"
        ));
        dataList.add(new GroupData(Config.GROUP_ID_KEYAKIZAKA46,
                mRes.getString(R.string.keyakizaka46),
                R.drawable.logo_keyakizaka46,
                "http://www.keyakizaka46.com/mob/sear/artiLis.php?site=k46o&ima=3848",
                null
        ));*/

        return dataList;
    }

    public ArrayList<SiteData> getBlogList() {
        ArrayList<SiteData> dataList = new ArrayList<>();

        /*dataList.add(new SiteData(Config.BLOG_ID_AKB48_OFFICIAL, Config.GROUP_ID_AKB48,
                mRes.getString(R.string.akb48_official_blog),
                R.drawable.logo_akb48,
                "http://s.ameblo.jp/akihabara48/",
                "http://s.ameblo.jp/akihabara48/",
                "http://feedblog.ameba.jp/rss/ameblo/akihabara48/rss20.xml"
        ));
        dataList.add(new SiteData(Config.BLOG_ID_AKB48_TEAM8, Config.GROUP_ID_AKB48,
                mRes.getString(R.string.akb48_team8_report),
                R.drawable.logo_akb48,
                "http://toyota-team8.jp/report/index.php",
                "http://toyota-team8.jp/report/sp/index.php",
                null
        ));*/

        /*dataList.add(new SiteData(Config.BLOG_ID_SKE48_STAFF, Config.GROUP_ID_SKE48,
                mRes.getString(R.string.ske48_secretariat_blog),
                R.drawable.logo_ske48,
                "http://www2.ske48.co.jp/blog/member/writer:secretariat",
                null,
                "http://www.ske48.co.jp/rss/blog_secretariat.xml"
        ));*/

        dataList.add(new SiteData(Config.BLOG_ID_SKE48_MEMBER, Config.GROUP_ID_SKE48,
                mRes.getString(R.string.ske48_official_blog),
                R.drawable.logo_ske48,
                "http://www2.ske48.co.jp/blog/memberList/",
                "",
                ""
        ));
        dataList.add(new SiteData(Config.BLOG_ID_SKE48_SELECTED, Config.GROUP_ID_SKE48,
                mRes.getString(R.string.ske48_selected_members_special_blog),
                R.drawable.logo_ske48,
                "http://s.ameblo.jp/ske48official/",
                "http://s.ameblo.jp/ske48official/",
                "http://feedblog.ameba.jp/rss/ameblo/ske48official/rss20.xml"
        ));
        dataList.add(new SiteData(Config.BLOG_ID_NMB48_OFFICIAL, Config.GROUP_ID_NMB48,
                mRes.getString(R.string.nmb48_official_blog),
                R.drawable.logo_nmb48,
                "http://s.ameblo.jp/nmb48/",
                "http://s.ameblo.jp/nmb48/",
                "http://feedblog.ameba.jp/rss/ameblo/nmb48/rss20.xml"
        ));
        /*dataList.add(new SiteData(Config.BLOG_ID_HKT48_OFFICIAL, Config.GROUP_ID_HKT48,
                mRes.getString(R.string.hkt48_official_blog),
                R.drawable.logo_hkt48,
                "http://s.ameblo.jp/hkt48",
                "http://s.ameblo.jp/hkt48/",
                "http://feedblog.ameba.jp/rss/ameblo/hkt48/rss20.xml"
        ));*/
        /*dataList.add(new SiteData(Config.BLOG_ID_NGT48_MANAGER, Config.GROUP_ID_NGT48,
                mRes.getString(R.string.ngt48_manager_blog),
                R.drawable.logo_ngt48,
                "http://lineblog.me/ngt48/",
                null,
                null
        ));*/
        /* // NGT48 포토로그는 사진을 외부 도메인에서 사용하지 못하게 막아놓았다. 2016-07-24
        dataList.add(new SiteData(Config.BLOG_ID_NGT48_PHOTOLOG, Config.GROUP_ID_NGT48,
                mRes.getString(R.string.ngt48_photo_log),
                R.drawable.logo_ngt48,
                "https://ngt48.com/photolog",
                "https://ngt48.com/photolog",
                null
        ));*/
        /*dataList.add(new SiteData(Config.BLOG_ID_NOGIZAKA46_OFFICIAL, Config.GROUP_ID_NOGIZAKA46,
                mRes.getString(R.string.nogizaka46_official_blog),
                R.drawable.logo_nogizaka46,
                "http://blog.nogizaka46.com/",
                "http://blog.nogizaka46.com/smph/",
                null
        ));
        dataList.add(new SiteData(Config.BLOG_ID_KEYAKIZAKA46_OFFICIAL, Config.GROUP_ID_KEYAKIZAKA46,
                mRes.getString(R.string.keyakizaka46_official_blog),
                R.drawable.logo_keyakizaka46,
                "http://www.keyakizaka46.com/mob/news/diarKiji.php?site=k46o&cd=member",
                "http://www.keyakizaka46.com/mob/news/diarShw.php?site=k46o&ima=0042&cd=member",
                null
        ));*/

        return dataList;
    }

    public ArrayList<SiteData> getYoutubeList() {
        ArrayList<SiteData> dataList = new ArrayList<>();

        dataList.add(new SiteData(Config.GROUP_ID_AKB48, Config.GROUP_ID_AKB48,
                mRes.getString(R.string.akb48),
                R.drawable.logo_akb48,
                "https://www.youtube.com/user/AKB48/videos",
                null,
                null
        ));
        dataList.add(new SiteData(Config.GROUP_ID_SKE48, Config.GROUP_ID_SKE48,
                mRes.getString(R.string.ske48),
                R.drawable.logo_ske48,
                "https://www.youtube.com/user/SKE48/videos",
                null,
                null
        ));
        dataList.add(new SiteData(Config.GROUP_ID_NMB48, Config.GROUP_ID_NMB48,
                mRes.getString(R.string.nmb48),
                R.drawable.logo_nmb48,
                "https://www.youtube.com/user/NMB48official/videos",
                null,
                null
        ));
        dataList.add(new SiteData(Config.GROUP_ID_HKT48, Config.GROUP_ID_HKT48,
                mRes.getString(R.string.hkt48),
                R.drawable.logo_hkt48,
                "https://www.youtube.com/user/HKT48/videos",
                null,
                null
        ));
        dataList.add(new SiteData(Config.GROUP_ID_NGT48, Config.GROUP_ID_NGT48,
                mRes.getString(R.string.ngt48),
                R.drawable.logo_ngt48,
                "https://www.youtube.com/channel/UCIfuY0NRq1szr_6tzFy23NQ/videos",
                null,
                null
        ));
        dataList.add(new SiteData(Config.GROUP_ID_JKT48, Config.GROUP_ID_JKT48,
                mRes.getString(R.string.jkt48),
                R.drawable.logo_jkt48,
                "https://www.youtube.com/user/JKT48/videos",
                null,
                null
        ));
        dataList.add(new SiteData(Config.GROUP_ID_SNH48, Config.GROUP_ID_SNH48,
                mRes.getString(R.string.snh48),
                R.drawable.logo_snh48,
                "https://www.youtube.com/user/SNH48UNOFFICIAL/videos",
                null,
                null
        ));

        return dataList;
    }

    public ArrayList<ElectionData> getElectionList() {
        ArrayList<ElectionData> dataList = new ArrayList<>();

        dataList.add(new ElectionData(8, 45, R.drawable.sousenkyo8,
                mRes.getString(R.string.election_catch_phase_45th),
                "2016.06.18",
                mRes.getString(R.string.election_place_45th),
                ""));
        dataList.add(new ElectionData(7, 41, R.drawable.sousenkyo7,
                mRes.getString(R.string.election_catch_phase_41st),
                "2015.06.06",
                mRes.getString(R.string.election_place_41th),
                ""));
        dataList.add(new ElectionData(6, 37, R.drawable.sousenkyo6,
                mRes.getString(R.string.election_catch_phase_37th),
                "2014.06.07",
                mRes.getString(R.string.election_place_37th),
                "https://www.youtube.com/watch?v=XymiE1CedxY"));
        dataList.add(new ElectionData(5, 32, R.drawable.sousenkyo5,
                mRes.getString(R.string.election_catch_phase_32nd),
                "2013.06.08",
                mRes.getString(R.string.election_place_32th),
                "https://www.youtube.com/watch?v=6nmnybaPMj0"));
        dataList.add(new ElectionData(4, 27, R.drawable.sousenkyo4,
                mRes.getString(R.string.election_catch_phase_27th),
                "2012.06.06",
                mRes.getString(R.string.election_place_27th),
                ""));
        dataList.add(new ElectionData(3, 22, R.drawable.sousenkyo3,
                mRes.getString(R.string.election_catch_phase_22nd),
                "2011.06.09",
                mRes.getString(R.string.election_place_22th),
                ""));
        dataList.add(new ElectionData(2, 17, R.drawable.sousenkyo2,
                mRes.getString(R.string.election_catch_phase_17th),
                "2010.06.09",
                mRes.getString(R.string.election_place_17th),
                "https://www.youtube.com/watch?v=eJs-fzRrDwk"));
        dataList.add(new ElectionData(1, 13, R.drawable.sousenkyo1,
                mRes.getString(R.string.election_catch_phase_13th),
                "2009.07.08",
                mRes.getString(R.string.election_place_13th),
                ""));

        return dataList;
    }

    /*public MemberData getMemberData(JSONObject json) {
        MemberData memberData = new MemberData();

        memberData.setGroupId(Util.getString(json, "groupId"));
        memberData.setGroupId(Util.getString(json, "groupId"));
        memberData.setTeamName(Util.getString(json, "teamName"));
        memberData.setGeneration(Util.getString(json, "generation"));

        memberData.setCaptain(Util.getString(json, "captain"));
        memberData.setViceCaptain(Util.getString(json, "viceCaptain"));
        memberData.setManager(Util.getString(json, "manager"));

        memberData.setName(Util.getString(json, "name"));
        memberData.setNameJa(Util.getString(json, "nameJa"));
        memberData.setNameId(Util.getString(json, "nameId"));
        memberData.setNameCn(Util.getString(json, "nameCn"));
        memberData.setNameEn(Util.getString(json, "nameEn"));
        memberData.setNameKo(Util.getString(json, "nameKo"));
        memberData.setFurigana(Util.getString(json, "furigana"));
        memberData.setPinyin(Util.getString(json, "pinyin"));

        memberData.setNickname(Util.getString(json, "nickname"));
        memberData.setNicknameJa(Util.getString(json, "nicknameJa"));

        memberData.setBirth(Util.getString(json, "birth"));
        memberData.setBirthday(Util.getString(json, "birthday"));
        memberData.setHometown(Util.getString(json, "hometown"));
        memberData.setDebut(Util.getString(json, "debut"));
        memberData.setDebutDay(Util.getString(json, "debutDay"));
        memberData.setInfo(Util.getString(json, "info"));

        memberData.setProfileUrl(Util.getString(json, "profileUrl"));
        memberData.setMobileUrl(Util.getString(json, "mobileUrl"));
        memberData.setThumbnailUrl(Util.getString(json, "thumbnailUrl"));
        memberData.setImageUrl(Util.getString(json, "imageUrl"));

        memberData.setPedia48Url(Util.getString(json, "pedia48Url"));
        memberData.setPedia48ThumbnailUrl(Util.getString(json, "pedia48ThumbnailUrl"));
        memberData.setPedia48ImageUrl(Util.getString(json, "pedia48ImageUrl"));

        memberData.setStage48Url(Util.getString(json, "stage48Url"));

        return memberData;
    }

    public JSONObject getMemberObject(MemberData data) {
        JSONObject object = new JSONObject();

        try {
            object.put("groupId", data.getGroupId());
            object.put("groupId", data.getGroupId());
            object.put("teamName", data.getTeamName());
            object.put("generation", data.getGeneration());

            object.put("catain", data.getCaptain());
            object.put("viceCaptain", data.getViceCaptain());
            object.put("manager", data.getManager());

            object.put("name", data.getName());
            object.put("nameJa", data.getNameJa());
            object.put("nameId", data.getNameId());
            object.put("nameCn", data.getNameCn());
            object.put("nameEn", data.getNameEn());
            object.put("nameKo", data.getNameKo());
            object.put("furigana", data.getFurigana());
            object.put("pinyin", data.getPinyin());

            object.put("nickname", data.getNickname());
            object.put("nicknameJa", data.getNicknameJa());

            object.put("birth", data.getBirth());
            object.put("birthday", data.getBirthday());
            object.put("hometown", data.getHometown());
            object.put("debut", data.getDebut());
            object.put("debutDay", data.getDebutDay());
            object.put("info", data.getInfo());

            object.put("profileUrl", data.getProfileUrl());
            object.put("mobileUrl", data.getMobileUrl());
            object.put("thumbnailUrl", data.getThumbnailUrl());
            object.put("imageUrl", data.getImageUrl());

            object.put("pedia48Url", data.getPedia48Url());
            object.put("pedia48ThumbnailUrl", data.getPedia48ThumbnailUrl());
            object.put("pedia48ImageUrl", data.getPedia48ImageUrl());

            object.put("stage48Url", data.getStage48Url());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

    public JSONObject getElectionObject(ElectionData data) {
        JSONObject object = new JSONObject();

        try {
            object.put("title", data.getTitle());
            object.put("description", data.getDescription());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

    public JSONObject getRankingObject(RankingData data) {
        JSONObject object = new JSONObject();

        try {
            object.put("electionTitle", data.getElectionTitle());
            object.put("ranking", data.getRanking());
            object.put("breakingVote", data.getBreakingVote());
            object.put("finalVote", data.getFinalVote());
            object.put("name", data.getName());
            object.put("teamName", data.getTeam());
            object.put("generation", data.getGeneration());
            object.put("imageUrl", data.getImageUrl());
            //Log.e(mTag, object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }*/
}
