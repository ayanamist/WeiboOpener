package com.ayanamist.weibo.opener;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

public class MainActivity extends Activity {
    private boolean canResolve(Intent intent) {
        return getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }

    private static Intent createIntentFromUri(String uri) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
    }

    private static Intent createUserInfoIntentFromUid(String uid) {
        return createIntentFromUri("sinaweibo://userinfo?uid=" + uid);
    }

    private static Intent createUserInfoIntentFromNick(String nick) {
        return createIntentFromUri("sinaweibo://userinfo?nick=" + nick);
    }

    private static Intent createDetailIntent(String mblogid) {
        return createIntentFromUri("sinaweibo://detail?mblogid=" + mblogid);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent originalIntent = getIntent();
        Uri uri = originalIntent.getData();
        List<String> pathSegments = uri.getPathSegments();
        int pathSegmentsSize = pathSegments.size();
        String msg = getResources().getString(R.string.unsupport_url);
        String uriHost = uri.getHost();
        if (uriHost.equalsIgnoreCase("weibo.cn") || uriHost.equalsIgnoreCase("m.weibo.cn") || uriHost.equalsIgnoreCase("www.weibo.cn")) {
            if (pathSegmentsSize == 2) {
                if (pathSegments.get(0).equals("n")) {
                    msg = openUserInfoByNick(pathSegments.get(1), originalIntent.getFlags());
                } else if (pathSegments.get(0).equals("u")) {
                    msg = openUserInfoByUid(pathSegments.get(1), originalIntent.getFlags());
                }
            }
        } else if (uriHost.equalsIgnoreCase("weibo.com") || uriHost.equalsIgnoreCase("www.weibo.com")) {
            if (pathSegmentsSize == 1) {
                msg = openUserInfoByUid(pathSegments.get(0), originalIntent.getFlags());
            } else if (pathSegmentsSize == 2) {
                msg = openDetailById(pathSegments.get(1), originalIntent.getFlags());
            }
        }
        if (msg.length() > 0) {
            Toast.makeText(getApplicationContext(), msg,
                    Toast.LENGTH_LONG).show();
        }
        setResult(RESULT_OK);
        finish();
    }

    private String openDetailById(String mblogid, int flags) {
        String msg = "";
        Intent weiboIntent = createDetailIntent(mblogid);
        weiboIntent.setFlags(flags);
        if (canResolve(weiboIntent)) {
            startActivity(weiboIntent);
        } else {
            msg = getResources().getString(R.string.unsupport_detail);
        }
        return msg;
    }

    private String openUserInfoByNick(String nick, int flags) {
        String msg = "";
        try {
            nick = URLDecoder.decode(nick, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
        }
        Intent weiboIntent = createUserInfoIntentFromNick(nick);
        weiboIntent.setFlags(flags);
        if (canResolve(weiboIntent)) {
            startActivity(weiboIntent);
        } else {
            msg = getResources().getString(R.string.unsupport_userinfo);
        }
        return msg;
    }

    private String openUserInfoByUid(String uid, int flags) {
        String msg = "";
        Intent weiboIntent = createUserInfoIntentFromUid(uid);
        weiboIntent.setFlags(flags);
        if (canResolve(weiboIntent)) {
            startActivity(weiboIntent);
        } else {
            msg = getResources().getString(R.string.unsupport_userinfo);
        }
        return msg;
    }

}
