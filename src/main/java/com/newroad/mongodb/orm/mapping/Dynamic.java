package com.newroad.mongodb.orm.mapping;

import java.io.Serializable;


/**
 * @info : property 中的动态内容
 * @author: tangzj
 * @data : 2013-6-8
 * @since : 1.5
 */
public class Dynamic implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2511215969179437293L;
	private Function function;
	private FunctionData data;

	public void addFunction(Function function, FunctionData data) {
		this.function = function;
		this.data = data;
	}

	/**
	 * 解析
	 */
	public Object parser(Object obj) throws Exception {
		Object res = null;
		if (function != null) {
			return function.parser(data, obj);
		}
		return res;
	}

	/*private Object parseMap(String name, Object obj) {
		if (obj == null)
			return text;
		String result = String.valueOf(text);
		if (text.contains(NodeParser.TAG)) {
			Map<String, Object> para = (Map<String, Object>) obj;
			for (String key : para.keySet()) {
				String regex = NodeParser.TAG + key + NodeParser.TAG;
				result = result
						.replaceAll(regex, String.valueOf(para.get(key))); // 替换动态参数
			}
		}
		return result;
	}

	private Object parseObject(Object obj) throws Exception {
		if (obj == null)
			return text;
		String result = String.valueOf(text);
		if (text.contains(NodeParser.TAG)) {
			Field[] fields = obj.getClass().getFields();
			for (Field fiel : fields) {
				String fileName = fiel.getName();
				String regex = NodeParser.TAG + fileName + NodeParser.TAG;
				if (result.indexOf(regex) != -1) {
					String gName = ParserWapper
							.getInvokeMethod("get", fileName);
					Method m = obj.getClass().getDeclaredMethod(gName);
					Object inv = m.invoke(obj);
					// 替换所属性的值
					result = result.replaceAll(regex, (String) inv);
				}
			}
		}
		return text;
	}

	private Object parseUnit(Object obj, String classType) {
		if (obj == null)
			return text;
		Object result = String.valueOf(text);
		if (text.contains(NodeParser.TAG)) {
			String regex = NodeParser.TAG + NodeParser.VALUE + NodeParser.TAG;
			result = ((String) result).replaceAll(regex, (String) obj);
		} else {
			if ("java.lang.Integer".equals(classType)) {
				String[] strResult=((String) result).replaceAll("\\[|\\]", "").split(",");
				int resultLen=strResult.length;
				Integer[] integerResult=new Integer[resultLen];
				for(int i=0;i<resultLen;i++){
					integerResult[i]=Integer.valueOf(strResult[i]);
				}
				result=integerResult;
			} else if ("java.lang.String".equals(classType)) {
				result = (String[])((String) result).replaceAll("\\[|\\]", "").split(",");
			}
		}
		return result;
	}*/

	public Function getFunction() {
		return function;
	}

	public FunctionData getData() {
		return data;
	}
}
