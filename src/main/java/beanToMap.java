import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class beanToMap {

    private final static String nameFrefix = "set";


    public static void beanToMap(Object obj, Map<String,Object> map) throws Exception {
        //获取当前类
        Class<?> cls  = obj.getClass();
        Method[] methods = cls.getDeclaredMethods(); //获取public 方法
        for (Method m : methods){
            String name = m.getName();
            if (name.startsWith("get")){
                name = name.substring(3);
                String first = name.substring(0,1).toLowerCase();//获取第一个参数转为小写
                String last = name.substring(1); //获取第一个参数后所有
                String key = first + last;
                //执行方法得到value
                Object value = m.invoke(obj);//执行get方法
                map.put(key,value);
            }
        }


    }



    public static Object mapTOBean(Map<String, Object> map, Object obj) throws Exception{
       Class classz = obj.getClass();

       if (!map.isEmpty()){
           for (Map.Entry<String, Object> keyValue :map.entrySet()) {
               // 得到map键值
               String propertyName = keyValue.getKey();
               // 得到map-value值
               Object value = keyValue.getValue();
                // 得到回属性名
               Field field = getClassField(classz, propertyName);

               if (field !=null) {
                   Class<?> fieldType = field.getType();
                   value  = convertValType(value, fieldType);
                   Method method = null;
                   // 得到属性set方法名
                   String setMethodName = convertKey(propertyName);
                   //得到方法
                   method = classz.getMethod(setMethodName, field.getType());
                   //判断是否能够执行（这个可以不要）
                   if (!method.isAccessible()) {
                       method.setAccessible(true);
                   }
                   method.invoke(obj, value);
               }


           }
       }
        return obj;


    }

    private static Field getClassField(Class<?> clazz, String fieldName) {
        // 传入类是Object类型或者是null直接return
        if (clazz == null || Object.class.getName().equals(clazz.getName())) {
            return null;
        }
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }

        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {// 简单的递归一下
            return getClassField(superClass, fieldName);
        }
        return null;
    }

    private static String convertKey(String propertyName) {
        // 将属性名第一个字母大写然后进行拼接
        String setMethodName = nameFrefix.concat(propertyName.substring(0, 1).toUpperCase().concat(propertyName.substring(1)));
        return setMethodName;
    }

    private static Object convertValType(Object value, Class<?> fieldType) {
        Object retVal = null;
        if (Long.class.getName().equals(fieldType.getName()) || long.class.getName().equals(fieldType.getName())) {
            retVal = Long.parseLong(value.toString());
        } else if (Integer.class.getName().equals(fieldType.getName())
                || int.class.getName().equals(fieldType.getName())) {
            retVal = Integer.parseInt(value.toString());
        } else if (Float.class.getName().equals(fieldType.getName())
                || float.class.getName().equals(fieldType.getName())) {
            retVal = Float.parseFloat(value.toString());
        } else if (Double.class.getName().equals(fieldType.getName())
                || double.class.getName().equals(fieldType.getName())) {
            retVal = Double.parseDouble(value.toString());
        } else if (Boolean.class.getName().equals(fieldType.getName())
                || boolean.class.getName().equals(fieldType.getName())) {
            retVal = Boolean.parseBoolean(value.toString());
        } else if (Character.class.getName().equals(fieldType.getName())
                || char.class.getName().equals(fieldType.getName())) {
            retVal = value;
        } else if(Date.class.getName().equals(fieldType.getName())){
            retVal = strConvertDate(value.toString());
        } else if(String.class.getName().equals(fieldType.getName())){
            retVal = value;
        }
        return retVal;
    }

    private static Date strConvertDate(String dateStr){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = null;
        try {
            parse = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parse;
    }







}
class T{
    public static void main(String[] a) throws Exception {
       /* Person p = new Person();
        p.setAge(18);
        p.setName("yys");
        p.setSex("m");
        Map<String,Object> map = new HashMap<String, Object>();
        beanToMap.beanToMap(p,map);
        System.out.println(map);*/
 /*       Map<String,Object> map = new HashMap<String, Object>();
        map.put("age",18);
        map.put("sex","f");
        map.put("name","yys");
        Person p = new Person();
        Person p1 = (Person) beanToMap.mapTOBean(map,p);
       System.out.println(p.toString());
        System.out.println(p1.toString());*/


    }
}
class Person{
    private String name;
    private int age;
    private String sex;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", sex='" + sex + '\'' +
                '}';
    }
}
class download{
    HttpURLConnection connection = null;

    public static void dowmloadImg(String imageUrl,String saveUrl){

        try {
            if (!new File(saveUrl).isDirectory()) {
                System.out.println("文件地址输入有误");
                return;
            }
            URL url = new URL(imageUrl);
            DataInputStream dataInputStream= new DataInputStream(url.openStream());

            FileOutputStream fileOutputStream = new FileOutputStream(new File(saveUrl + System.currentTimeMillis() + ".jpg"));
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            byte[] context=output.toByteArray();
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();




        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        dowmloadImg("https://i1.hdslb.com/bfs/archive/4596b8c7f5028d7f14086d6d56552fced1626271.jpg@160w_100h.jpg","F:/test/");

    }




}
class Datepull{
    private static final String IMGURL_REG = "http://.+?\\.(gif|jpeg|png|jpg|bmp)";
    private static final String IMGSRC_REG = "[a-zA-z]+://[^\\s]*";
    private static String getHtml(String url) throws Exception {
        URL u = new URL(url);
        URLConnection connection =u.openConnection();
        InputStream inputStream = connection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader br=new BufferedReader(inputStreamReader);

        String line;
        StringBuffer sb = new StringBuffer();
        while ((line = br.readLine()) != null) {
            sb.append(line,0,line.length());//添加到StringBuffer中
            sb.append('\n');//添加换行符
        }
        inputStream.close();
        br.close();
        inputStreamReader.close();


        return  sb.toString();
    }
    private static List<String> getImageUrl(String html){
        Matcher matcher=Pattern.compile(IMGURL_REG).matcher(html);
        List<String>listimgurl=new ArrayList<String>();
        while (matcher.find()){
            listimgurl.add(matcher.group());
        }
        return listimgurl;
    }
  /*  private static List<String> getImageSrc(List<String> listimageurl){
        List<String> listImageSrc=new ArrayList<String>();
        for (String image:listimageurl){
            Matcher matcher=Pattern.compile(IMGSRC_REG).matcher(image);
            while (matcher.find()){
                listImageSrc.add(matcher.group().substring(0, matcher.group().length()-1));
            }
        }
        return listImageSrc;

    }*/
    private static void Download(List<String> listImgSrc) {
        try{
            Date begindate = new Date();
            List<String> src = listImgSrc.stream().filter(str -> str.lastIndexOf(".png") != -1 || str.lastIndexOf(".jpg") != -1).collect(Collectors.toList());
            for (String url:src) {
                Date begindate2 = new Date();
                String imageName = url.substring(url.lastIndexOf("/") + 1, url.length());
                URL uri = new URL(url);
                InputStream in = uri.openStream();
                FileOutputStream fo = new FileOutputStream(new File("F:\\test\\"+imageName));
                byte[] buf = new byte[1024];
                int length = 0;
                System.out.println("开始下载:" + url);
                while ((length = in.read(buf, 0, buf.length)) != -1) {
                    fo.write(buf, 0, length);
                }
                //关闭流
                in.close();
                fo.close();
                System.out.println(imageName + "下载完成");
                //结束时间
                Date overdate2 = new Date();
                double time = overdate2.getTime() - begindate2.getTime();
                System.out.println("耗时：" + time / 1000 + "s");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }


    }



    public static void main(String[] args) throws Exception {
        String html =getHtml("https://image.baidu.com/search/index?tn=baiduimage&ipn=r&ct=201326592&cl=2&lm=-1&st=-1&fm=result&fr=&sf=1&fmq=1560949139420_R&pv=&ic=&nc=1&z=&hd=&latest=&copyright=&se=1&showtab=0&fb=0&width=&height=&face=0&istype=2&ie=utf-8&sid=&word=%E6%96%B0%E5%9E%A3%E7%BB%93%E8%A1%A3&f=3&oq=xiny&rsp=0");
        List<String> imageUrl = getImageUrl(html);
       // List<String> imageSrc= getImageSrc(imageUrl);
        Download(imageUrl);

      /*  List<String> strArr = Arrays.asList("1", "2", "3", "4");

        strArr.stream().filter(str ->{
            return "2".equals(str)?true:false;
        });
        System.out.println(strArr);*/

    }
}
