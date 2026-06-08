package org.graphicsEditor.global;

import java.util.*;

// 다국어 문자열 관리 — Singleton + Observer
// GStrings.get("key") 로 현재 언어 문자열 조회
// GStrings.setLang("en") 호출 시 등록된 리스너들에게 자동 알림 → UI 갱신
public class GStrings {

    // attributes
    private static String currentLang = "ko";
    private static final Vector<Runnable>                    listeners = new Vector<>();
    private static final Map<String, Map<String, String>> resources = new HashMap<>();

    // static initializer — 언어 데이터 등록
    static {
        // 한국어
        Map<String, String> ko = new HashMap<>();
        ko.put("shape", "도형"); ko.put("language", "언어"); ko.put("settings", "설정"); ko.put("home", "홈"); ko.put("text", "텍스트");
        ko.put("file", "File"); ko.put("edit", "Edit"); ko.put("sprite", "Sprite"); ko.put("layer", "Layer");
        ko.put("frame", "Frame"); ko.put("select", "Select"); ko.put("view", "View"); ko.put("help", "Help");
        ko.put("profile", "프로필"); ko.put("goUnreal", "Go Unreal"); ko.put("addSupporter", "서포터 추가");
        ko.put("newFile", "New File..."); ko.put("openFile", "Open File..."); ko.put("recoverFiles", "Recover Files.");
        ko.put("recentFiles", "Recent files:"); ko.put("recentFolders", "Recent folders:");
        ko.put("noRecentFiles", "최근 파일이 없습니다."); ko.put("noRecentFolders", "최근 폴더가 없습니다.");
        ko.put("back", "< 뒤로"); ko.put("langSettings", "언어 설정"); ko.put("settingsTitle", "설정");
        ko.put("rendering", "렌더링"); ko.put("antiAlias", "안티 앨리어싱"); ko.put("showGrid", "그리드 표시");
        ko.put("strokeWidth", "기본 선 두께"); ko.put("resetDefault", "기본값으로 초기화");
        ko.put("gallery", "내 작업물"); ko.put("galleryEmpty", "아직 저장된 작업물이 없어요.");
        ko.put("close", "닫기"); ko.put("cancel", "취소"); ko.put("save", "저장");
        ko.put("langChanged", "으로 변경되었습니다.");
        ko.put("undo", "Undo"); ko.put("redo", "Redo"); ko.put("undoHistory", "Undo History");
        ko.put("cut", "Cut"); ko.put("copy", "Copy"); ko.put("copyMerged", "Copy Merged");
        ko.put("paste", "Paste"); ko.put("pasteSpecial", "Paste Special"); ko.put("delete", "Delete");
        ko.put("fill", "Fill"); ko.put("stroke", "Stroke");
        ko.put("rotate", "Rotate"); ko.put("flipH", "Flip Horizontal"); ko.put("flipV", "Flip Vertical");
        ko.put("transform", "Transform"); ko.put("shift", "Shift");
        resources.put("ko", ko);

        // 영어
        Map<String, String> en = new HashMap<>();
        en.put("shape", "Shape"); en.put("language", "Language"); en.put("settings", "Settings"); en.put("home", "Home"); en.put("text", "Text");
        en.put("file", "File"); en.put("edit", "Edit"); en.put("sprite", "Sprite"); en.put("layer", "Layer");
        en.put("frame", "Frame"); en.put("select", "Select"); en.put("view", "View"); en.put("help", "Help");
        en.put("profile", "Profile"); en.put("goUnreal", "Go Unreal"); en.put("addSupporter", "Add Supporter");
        en.put("newFile", "New File..."); en.put("openFile", "Open File..."); en.put("recoverFiles", "Recover Files.");
        en.put("recentFiles", "Recent files:"); en.put("recentFolders", "Recent folders:");
        en.put("noRecentFiles", "No recent files."); en.put("noRecentFolders", "No recent folders.");
        en.put("back", "< Back"); en.put("langSettings", "Language"); en.put("settingsTitle", "Settings");
        en.put("rendering", "Rendering"); en.put("antiAlias", "Anti-aliasing"); en.put("showGrid", "Show Grid");
        en.put("strokeWidth", "Default Stroke Width"); en.put("resetDefault", "Reset to Default");
        en.put("gallery", "My Works"); en.put("galleryEmpty", "No saved works yet.");
        en.put("close", "Close"); en.put("cancel", "Cancel"); en.put("save", "Save");
        en.put("langChanged", " has been selected.");
        en.put("undo", "Undo"); en.put("redo", "Redo"); en.put("undoHistory", "Undo History");
        en.put("cut", "Cut"); en.put("copy", "Copy"); en.put("copyMerged", "Copy Merged");
        en.put("paste", "Paste"); en.put("pasteSpecial", "Paste Special"); en.put("delete", "Delete");
        en.put("fill", "Fill"); en.put("stroke", "Stroke");
        en.put("rotate", "Rotate"); en.put("flipH", "Flip Horizontal"); en.put("flipV", "Flip Vertical");
        en.put("transform", "Transform"); en.put("shift", "Shift");
        resources.put("en", en);

        // 일본어
        Map<String, String> ja = new HashMap<>();
        ja.put("shape", "図形"); ja.put("language", "言語"); ja.put("settings", "設定"); ja.put("home", "ホーム"); ja.put("text", "テキスト");
        ja.put("file", "File"); ja.put("edit", "Edit"); ja.put("sprite", "Sprite"); ja.put("layer", "Layer");
        ja.put("frame", "Frame"); ja.put("select", "Select"); ja.put("view", "View"); ja.put("help", "Help");
        ja.put("profile", "プロフィール"); ja.put("goUnreal", "Go Unreal"); ja.put("addSupporter", "サポーター追加");
        ja.put("newFile", "New File..."); ja.put("openFile", "Open File..."); ja.put("recoverFiles", "Recover Files.");
        ja.put("recentFiles", "最近のファイル:"); ja.put("recentFolders", "最近のフォルダ:");
        ja.put("noRecentFiles", "最近のファイルはありません。"); ja.put("noRecentFolders", "最近のフォルダはありません。");
        ja.put("back", "< 戻る"); ja.put("langSettings", "言語設定"); ja.put("settingsTitle", "設定");
        ja.put("rendering", "レンダリング"); ja.put("antiAlias", "アンチエイリアス"); ja.put("showGrid", "グリッド表示");
        ja.put("strokeWidth", "デフォルト線の太さ"); ja.put("resetDefault", "デフォルトに戻す");
        ja.put("gallery", "マイ作品"); ja.put("galleryEmpty", "保存された作品はまだありません。");
        ja.put("close", "閉じる"); ja.put("cancel", "キャンセル"); ja.put("save", "保存");
        ja.put("langChanged", "に変更されました。");
        ja.put("undo", "Undo"); ja.put("redo", "Redo"); ja.put("undoHistory", "Undo History");
        ja.put("cut", "Cut"); ja.put("copy", "Copy"); ja.put("copyMerged", "Copy Merged");
        ja.put("paste", "Paste"); ja.put("pasteSpecial", "Paste Special"); ja.put("delete", "Delete");
        ja.put("fill", "Fill"); ja.put("stroke", "Stroke");
        ja.put("rotate", "Rotate"); ja.put("flipH", "Flip Horizontal"); ja.put("flipV", "Flip Vertical");
        ja.put("transform", "Transform"); ja.put("shift", "Shift");
        resources.put("ja", ja);
    }

    // methods
    public static String get(String key) {
        Map<String, String> map = resources.getOrDefault(currentLang, resources.get("ko"));
        return map.getOrDefault(key, key);
    }

    public static String  getLang()              { return currentLang; }
    public static void    addListener(Runnable r) { listeners.add(r); }

    public static void setLang(String lang) {
        if (!resources.containsKey(lang)) return;
        currentLang = lang;
        for (Runnable r : listeners) r.run();
    }
}