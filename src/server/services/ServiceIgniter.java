package server.services;

import databases.exception.NothingToTakeException;

import org.eclipse.jetty.server.Server;
import server.comm.DataMap;
import server.comm.RestProcessor;
import server.response.Response;
import server.response.ResponseConst;
import server.rest.DataMapUtil;
import server.rest.RestConstant;
import server.rest.RestUtil;
import services.CommonSVC;
import spark.Filter;
import spark.Request;
import spark.Service;
import spark.Spark;
import spark.embeddedserver.EmbeddedServers;
import spark.embeddedserver.jetty.EmbeddedJettyFactory;
import utils.Log;

import java.security.MessageDigest;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

/**
 * @author 함의진
 * @version 2.0.0
 * 서버 실행을 위한 이그니션 클래스
 * @description (version 2.0.0) Json 스트링 Transformer를 Lambda 식으로 대체함.
 * Jul-21-2017
 */
public class ServiceIgniter {

    private Service service;

    private CommonSVC commonSVC;

    public static final int LOG_DEFAULT_LENGTH = 500;
    private int logLength = LOG_DEFAULT_LENGTH;

    public static ServiceIgniter instance;

    public static ServiceIgniter getInstance() {
        if(instance == null) instance = new ServiceIgniter();
        return instance;
    }

    private ServiceIgniter(){
        EmbeddedServers.add(EmbeddedServers.Identifiers.JETTY, new EmbeddedJettyFactory((i, j, k) -> {
            Server server = new Server();
            server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", 5000000);
            return server;
        }));

        commonSVC = new CommonSVC();
    }

    public void igniteServiceServer(){

        /**
         * PORT 세팅 및 Response 헤더 세팅
         */
        service = Service.ignite().port(RestConstant.REST_SERVICE);

        service.before((req, res)-> {
            DataMap map = RestProcessor.makeProcessData(req.raw());
            Log.e("[Connection] Service Server [" + Calendar.getInstance().getTime().toString() + "] :: [" + req.pathInfo() + "] FROM [" + RestUtil.extractIp(req.raw()) + "] :: " + map);
            Log.i("[" + Calendar.getInstance().getTime().toString() + "] :: [" + req.pathInfo() + "] FROM [" + RestUtil.extractIp(req.raw()) + "] :: " + map);

            res.type(RestConstant.RESPONSE_TYPE_JSON);
        });

        service.path("/admin", ()->{
            service.get("/login", ((request, response) -> {
                DataMap map = RestProcessor.makeProcessData(request.raw());

                if(DataMapUtil.isValid(map, "id") && DataMapUtil.isValid(map, "pwd")) {
                    String id = map.getString("id");
                    String MD5 ="";
                    MessageDigest pwd = MessageDigest.getInstance("MD5");
                    pwd.update(map.getString("pwd").getBytes());
                    byte byteData[] = pwd.digest();

                    StringBuffer sb = new StringBuffer();

                    for(int i = 0 ; i < byteData.length ; i++){

                        sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));

                    }
                    MD5 = sb.toString();

                    Log.e("hashed string :::::: "+MD5);

                    try {
                        DataMap ret = commonSVC.AdminLogin(id, MD5);
                        return new Response(ResponseConst.CODE_SUCCESS, ResponseConst.MSG_SUCCESS, ret);
                    }catch (NothingToTakeException e){
                        return new Response(ResponseConst.CODE_INVALID_PARAM, ResponseConst.MSG_INVALID_PARAM);
                    }
                }else{
                    return new Response(ResponseConst.CODE_FAILURE, ResponseConst.MSG_FAILURE);
                }

            }), RestUtil::toJson);

            service.get("/getAdmin", ((request, response) -> {
                DataMap map = RestProcessor.makeProcessData(request.raw());

                if(DataMapUtil.isValid(map, "no")) {
                    int no = map.getInt("no");
                    try {
                        DataMap ret = commonSVC.getAdminByNumber(no);
                        return new Response(ResponseConst.CODE_SUCCESS, ResponseConst.MSG_SUCCESS, ret);
                    }catch (NothingToTakeException e){
                        return new Response(ResponseConst.CODE_INVALID_PARAM, ResponseConst.MSG_INVALID_PARAM);
                    }
                }else{
                    return new Response(ResponseConst.CODE_FAILURE, ResponseConst.MSG_FAILURE);
                }

            }), RestUtil::toJson);
        });

        Log.i("[" + Calendar.getInstance().getTime().toString() + "]" + "[INFO] :: Service Server ignited.");

    }

}
