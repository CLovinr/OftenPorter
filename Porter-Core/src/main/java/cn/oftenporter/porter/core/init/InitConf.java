package cn.oftenporter.porter.core.init;

/**
 * 初始化的配置。
 * Created by https://github.com/CLovinr on 2016/7/23.
 */
public interface InitConf
{

    public static final String NAME_WEBPORTER_PATHS = "porterPaths";
    public static final String NAME_WEBPORTER_PATHS_isSys = "isSys";
    public static final String NAME_WEBPORTER_PATHS_porters = "porters";
    public static final String NAME_TIED_PREFIX = "tiedPrefix";
    public static final String NAME_STATE_LISTENERS = "stateListeners";
    public static final String NAME_ENCODING = "encoding";
    public static final String NAME_NOT_FOUND_THINKTYPE = "notFoundThinkType";
    public static final String NAME_GLOBAL_CHECK = "globleCheck";
    public static final String NAME_RESPONSE_LOSEDARGS = "responseLosedArgs";
    public static final String NAME_EXLISTENER = "exListener";

    public static final String PARAMS_NAME_PORTER = "porter";
    public static final String PARAMS_NAME_USER = "user";


    /**
     * 获取用户自定义的配置参数。
     */
    InitParamSource userConf();

    /**
     * 获取框架自身的配置参数。
     */
    InitParamSource conf();
}
