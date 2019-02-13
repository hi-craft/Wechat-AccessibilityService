package com.yh.autocontrolwechat;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.util.List;

public class WechatUtils {

    public static String NAME;
    public static String CONTENT;

    /**
     * 在当前页面查找文字内容并点击
     *
     * @param text
     */

    public static void findTextAndClick(AccessibilityService accessibilityService, String text) {
//        List<AccessibilityWindowInfo> accessibilityWindowInfo = accessibilityService.getWindows();
//        AccessibilityNodeInfo accessibilityNodeInfo =  accessibilityWindowInfo.get(2).getRoot();
        AccessibilityNodeInfo accessibilityNodeInfo = accessibilityService.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return;
        }

        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null && (text.equals(nodeInfo.getText()) || text.equals(nodeInfo.getContentDescription()))) {
                    performClick(nodeInfo);
                    break;
                }
            }
        }
    }

    /**
     * 查找view
     *
     * @param accessibilityService
     * @param id
     */
    public static boolean findViewId(AccessibilityService accessibilityService, String id) {

        AccessibilityNodeInfo accessibilityNodeInfo = accessibilityService.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return false;
        }

        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
        return nodeInfoList != null && !nodeInfoList.isEmpty();
    }

    /**
     * 检查viewId进行点击
     *
     * @param accessibilityService
     * @param id
     */
    public static void findViewIdAndClick(AccessibilityService accessibilityService, String id) {

//        List<AccessibilityWindowInfo> accessibilityWindowInfo = accessibilityService.getWindows();
//        AccessibilityNodeInfo accessibilityNodeInfo =  accessibilityWindowInfo.get(2).getRoot();

        AccessibilityNodeInfo accessibilityNodeInfo = accessibilityService.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return;
        }

        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    performClick(nodeInfo);
                    break;
                }
            }
        }
    }

    /**
     * 查找控件，并将焦点放在该空间上
     *
     * @param accessibilityService
     * @param id
     */
    public static void findViewIdAndFouce(AccessibilityService accessibilityService, String id) {

        AccessibilityNodeInfo accessibilityNodeInfo = accessibilityService.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return;
        }

        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                    break;
                }
            }
        }
    }


    /**
     * 根据id查找控件，并将内容粘贴上去
     *
     * @param accessibilityService
     * @param id
     * @param content
     * @return
     */
    public static boolean findViewByIdAndPasteContent(AccessibilityService accessibilityService, String id, String content) {
        AccessibilityNodeInfo rootNode = accessibilityService.getRootInActiveWindow();
        if (rootNode != null) {
            List<AccessibilityNodeInfo> editInfo = rootNode.findAccessibilityNodeInfosByViewId(id);
            if (editInfo != null && !editInfo.isEmpty()) {
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, content);
                editInfo.get(0).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * 根据id查找控件上的文字描述
     *
     * @param accessibilityService
     * @param id
     * @return
     */
    public static String findTextById(AccessibilityService accessibilityService, String id) {
        AccessibilityNodeInfo rootInfo = accessibilityService.getRootInActiveWindow();
        if (rootInfo != null) {
            List<AccessibilityNodeInfo> userNames = rootInfo.findAccessibilityNodeInfosByViewId(id);
            if (userNames != null && userNames.size() > 0) {
                String name = userNames.get(0).getText().toString();
                return name;
            }
        }
        return null;
    }


    /**
     * 在当前页面查找对话框文字内容并点击
     *
     * @param text1 默认点击text1
     * @param text2
     */
    public static void findDialogAndClick(AccessibilityService accessibilityService, String text1, String text2) {

        AccessibilityNodeInfo accessibilityNodeInfo = accessibilityService.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return;
        }

        List<AccessibilityNodeInfo> dialogWait = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text1);
        List<AccessibilityNodeInfo> dialogConfirm = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text2);
        if (!dialogWait.isEmpty() && !dialogConfirm.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : dialogWait) {
                if (nodeInfo != null && text1.equals(nodeInfo.getText())) {
                    performClick(nodeInfo);
                    break;
                }
            }
        }
    }

    /**
     * 将内容粘贴到对应的控件上
     *
     * @param accessibilityService
     * @param nodeInfo
     * @param content
     */
    public static void pastContent(AccessibilityService accessibilityService, AccessibilityNodeInfo nodeInfo, String content) {
        Bundle arguments = new Bundle();
        arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT,
                AccessibilityNodeInfo.MOVEMENT_GRANULARITY_WORD);
        arguments.putBoolean(AccessibilityNodeInfo.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN, true);
        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY, arguments);
        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
        ClipData clip = ClipData.newPlainText("label", content);
        ClipboardManager clipboardManager = (ClipboardManager) accessibilityService.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(clip);
        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
    }

    //模拟点击事件
    public static void performClick(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return;
        }
        if (nodeInfo.isClickable()) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
            performClick(nodeInfo.getParent());
        }
    }

    //    public static void performScroll() {
//
//           this.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
//
//    }
    //模拟返回事件
    public static void performBack(AccessibilityService service) {
        if (service == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        }
    }
}
