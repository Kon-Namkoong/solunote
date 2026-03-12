package com.vol.solunote.comm.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class MenuSortHandler {
	
	public JSONObject setMenuJson(List<Map<String, Object>> list, String parentkey, String selfKey) throws JSONException {
		
		JSONObject json = new JSONObject();
		
		for (Map<String, Object> map : list) {
			if((int)map.get(parentkey) ==0 ) {
				
				JSONObject subJson = new JSONObject(map);
				subJson.put("list", new JSONArray());
				
				json.put((String)map.get(selfKey), subJson);
			} else {
				
				String pKey = (int) map.get(parentkey)+"";
				JSONArray  listObj = (JSONArray) ((JSONObject)json.get(pKey)).get("list");
				
				JSONObject subJson = new JSONObject(map);
				subJson.put("pName", ((JSONObject)json.get(pKey)).get("MENU_NAME"));
				subJson.put("PUseYn", ((JSONObject)json.get(pKey)).get("MENU_USEYN"));
				subJson.put("PDownloadYn", ((JSONObject)json.get(pKey)).get("DOWN_RIGHTS"));
				subJson.put("pMenuOrder", ((JSONObject)json.get(pKey)).get("MENU_NO"));
				
				listObj.put(subJson);
			}
		}
		
		return json;
	}
	
	public ArrayList<JSONObject> setJsonSort(JSONObject json) throws JSONException {

		ArrayList<JSONObject> objList = new ArrayList<>();

		Iterator<?> keys = json.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();

			JSONArray jsonArray = (JSONArray) ((JSONObject) json.get(key)).get("list");

			int length = jsonArray.length();
			for (int i = 0; i < length; i++) {
				objList.add((JSONObject) jsonArray.get(i));
			}

		}

		Collections.sort(objList, new MyJSONComparator());

		return objList;
	}
	
}

class MyJSONComparator implements Comparator<JSONObject> {

@Override
public int compare(JSONObject o1, JSONObject o2) {
    String v1 = null;
    String v3 = null;
    try {
		v1 = (int) o1.get("pMenuOrder")+"";
		v3 = (int) o2.get("pMenuOrder")+"";
	} catch (JSONException e) {
		// Auto-generated catch block
		e.printStackTrace();
	}
    
    return v1.compareTo(v3);
}

}