package com.youboy.search.domain;

import net.sf.json.JSONObject;

public class EsRequestBean {

	private RequestType requestType;
	private String index;
	private String type;
	private String id;
	private JSONObject indexValue;

	public EsRequestBean JsonToBean(String json) {
		EsRequestBean esRequestBean = new EsRequestBean();
		try {
			if (json.substring(0, 10).contains("delete")) {
				JSONObject jsonIndex = JSONObject.fromObject(json);
				JSONObject jsonToEs = (JSONObject) jsonIndex.get("delete");
				esRequestBean.setRequestType(RequestType.DELETE);
				esRequestBean.setIndex(jsonToEs.get("_index").toString());
				esRequestBean.setType(jsonToEs.get("_type").toString());
				esRequestBean.setId(jsonToEs.get("_id").toString());
			} else {
				String[] valueList = json.split("\n");
				String IndexString = valueList[0];
				String ValueString = valueList[1];
				JSONObject jsonIndex = JSONObject.fromObject(IndexString);
				JSONObject jsonValue = JSONObject.fromObject(ValueString);

				JSONObject jsonToEs = (JSONObject) jsonIndex.get("index");
				esRequestBean.setRequestType(RequestType.INDEX);
				esRequestBean.setIndex(jsonToEs.get("_index").toString());
				esRequestBean.setType(jsonToEs.get("_type").toString());
				esRequestBean.setId(jsonToEs.get("_id").toString());
				esRequestBean.setIndexValue(jsonValue);
			}
		} catch (Exception e) {
			return null;
		}
		return esRequestBean;
	}

	public RequestType getRequestType() {
		return requestType;
	}

	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public JSONObject getIndexValue() {
		return indexValue;
	}

	public void setIndexValue(JSONObject indexValue) {
		this.indexValue = indexValue;
	}

    public enum RequestType {
        DELETE, INDEX
    }

}
