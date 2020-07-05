package net.snakefangox.fasterthanc.tools;

import net.minecraft.util.Language;

public class TranslateHelper {

	public static String translateIfNeeded(String s) {
		if (Language.getInstance().hasTranslation(s)) {
			return Language.getInstance().get(s);
		}else{
			return s;
		}
	}
}
