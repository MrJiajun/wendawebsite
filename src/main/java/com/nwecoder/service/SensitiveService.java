package com.nwecoder.service;


import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/6 0006.
 */
@Service
public class SensitiveService implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);

    private TrieNode rootNode = new TrieNode();

    private class TrieNode{
        //是不是关键词的结尾
        private boolean end = false;

        //当前节点下所有的子节点
        private Map<Character, TrieNode> subNode = new HashMap<Character, TrieNode>();

        public void addSubNode(Character key, TrieNode node){
            subNode.put(key, node);
        }

        TrieNode getSubNode(Character key){
            return subNode.get(key);
        }

        boolean isKeywordEnd(){
            return end;
        }

        void setKeywordEnd(boolean end){
            this.end = end;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        rootNode = new TrieNode();

        //初始化
        try{
            InputStream InpS = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader reader = new InputStreamReader(InpS);
            BufferedReader bufferedInputStream = new BufferedReader(reader);

            String lineTxt;
            while((lineTxt = bufferedInputStream.readLine()) != null){
                addWord(lineTxt.trim());
            }
            reader.close();
        }catch (Exception e){
            logger.error("读取敏感词文件失败" + e.getMessage());
        }
    }

    private boolean isSymbol(char c){
        int ic = (int) c;
        return !CharUtils.isAsciiAlphanumeric(c)  &&  (ic < 0x2E80 || ic > 0x9FFF);//ic < 0x2E80 || ic > 0x9FFF,.在这个范围内的是非东亚文字
    }

    //增加关键词
    private void addWord(String lineTxt){
        TrieNode tempNode = rootNode;
        for(int i = 0; i < lineTxt.length(); ++i){
            Character c = lineTxt.charAt(i);

            TrieNode node = tempNode.getSubNode(c);

            if(node == null){
                node = new TrieNode();
                tempNode.addSubNode(c, node);
            }
            tempNode = node;

            if(i == lineTxt.length() - 1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /*
     * 过滤敏感词
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return text;
        }
        StringBuilder result = new StringBuilder();
        String replacement = "***";
        TrieNode tempNode = rootNode;
        int begin = 0;
        int position = 0;
        while (position < text.length()){
            char c = text.charAt(position);
            if(isSymbol(c))
            {
                if(tempNode == rootNode){
                    result.append(c);
                    ++begin;
                }
                ++position;
                continue;
            }

            tempNode = tempNode.getSubNode(c);

            if(tempNode == null){
                result.append(text.charAt(begin));
                position = begin + 1;
                begin = position;
                tempNode = rootNode;
            }else if(tempNode.isKeywordEnd())        {
                result.append(replacement);
                position = position + 1;
                begin = position;
                tempNode = rootNode;
            }else{
                ++position;
            }
        }

        result.append(text.substring(begin));
        return result.toString();
    }

    public static void main(String[] argv){
        SensitiveService s = new SensitiveService();
        s.addWord("色情");
        s.addWord("赌博");
        s.addWord("好色");
        System.out.print(s.filter("hi 你●好●色●情！"));
    }
}
