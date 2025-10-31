package com.xkball.shoot_screen.utils;

import com.mojang.blaze3d.platform.IconSet;
import com.mojang.logging.LogUtils;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.W32APIOptions;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;

import javax.imageio.ImageIO;

public class TheSystemTray {
    
    private static final Logger LOGGER = LogUtils.getLogger();
    private NOTIFYICONDATA nid;
    
    public TheSystemTray() {
        this.nid = createNotifyIconData();
    }
    
    public void closeTray(){
        User32.INSTANCE.DestroyIcon(nid.hIcon);
        User32.INSTANCE.DestroyWindow(nid.hWnd);
        Shell32.INSTANCE.Shell_NotifyIcon(NIM_DELETE, nid);
    }
    
    public static final int NIM_ADD = 0x00000000;
    public static final int NIM_MODIFY = 0x00000001;
    public static final int NIM_DELETE = 0x00000002;
    public static final int NIF_MESSAGE = 0x00000001;
    public static final int NIF_ICON = 0x00000002;
    public static final int NIF_TIP = 0x00000004;
    
    public interface Shell32 extends Library {
        Shell32 INSTANCE = Native.load("shell32", Shell32.class, W32APIOptions.DEFAULT_OPTIONS);
        boolean Shell_NotifyIcon(int dwMessage, NOTIFYICONDATA lpData);
    }
    
    public interface ExtendedUser32 extends User32 {
        ExtendedUser32 INSTANCE = Native.load("user32", ExtendedUser32.class, W32APIOptions.DEFAULT_OPTIONS);
        HICON CreateIconIndirect(WinGDI.ICONINFO piconinfo);
        HMENU CreatePopupMenu();
        boolean AppendMenuW(HMENU menu, int uFlags, WPARAM uIDNewItem, String lpNewItem);
        int TrackPopupMenu(HMENU hMenu, int uFlags, int x, int y, int nReserved, HWND hWnd, RECT prcRect);
    }
    
    public interface ExtendedGDI32 extends GDI32 {
        ExtendedGDI32 INSTANCE = Native.load("gdi32", ExtendedGDI32.class, W32APIOptions.DEFAULT_OPTIONS);
        WinDef.HBITMAP CreateBitmap(int nWidth, int nHeight, int cPlanes, int cBitsPerPel, byte[] lpvBits);
    }
    
    public static class NOTIFYICONDATA extends Structure {
        public int cbSize;
        public WinDef.HWND hWnd;
        public int uID;
        public int uFlags;
        public int uCallbackMessage;
        public WinDef.HICON hIcon;
        public char[] szTip = new char[128];
        public int dwState;
        public int dwStateMask;
        public char[] szInfo = new char[256];
        public int uTimeoutOrVersion;
        public char[] szInfoTitle = new char[64];
        public int dwInfoFlags;
        public Guid.GUID guidItem;
        public WinDef.HICON hBalloonIcon;
        
        @Override
        protected java.util.List<String> getFieldOrder() {
            return java.util.Arrays.asList("cbSize", "hWnd", "uID", "uFlags",
                    "uCallbackMessage", "hIcon", "szTip", "dwState", "dwStateMask",
                    "szInfo", "uTimeoutOrVersion", "szInfoTitle", "dwInfoFlags",
                    "guidItem", "hBalloonIcon");
        }
    }
    
    public NOTIFYICONDATA createNotifyIconData() {
        var name = "MinePaper Engine";
        WinDef.HWND hwnd = createMessageWindow(name);
        WinDef.HICON hIcon = createIcon();
        
        NOTIFYICONDATA nid = new NOTIFYICONDATA();
        nid.cbSize = nid.size();
        nid.hWnd = hwnd;
        nid.uID = 1;
        nid.uFlags = NIF_MESSAGE | NIF_ICON | NIF_TIP;
        nid.uCallbackMessage = WinUser.WM_USER + 1;
        nid.hIcon = hIcon;
        System.arraycopy(name.toCharArray(), 0, nid.szTip, 0, name.length());
        boolean success = Shell32.INSTANCE.Shell_NotifyIcon(NIM_ADD, nid);
        
        return nid;
    }
    
    private static WinDef.HWND createMessageWindow(String className) {
        final User32 user32 = User32.INSTANCE;
        WinUser.WNDCLASSEX wndclass = new WinUser.WNDCLASSEX();
        wndclass.lpszClassName = className;
        wndclass.lpfnWndProc =(WinUser.WindowProc) (hwnd, uMsg, wParam, lParam) -> {
            //LOGGER.debug("{},{},{},{}",hwnd,uMsg,wParam,lParam);
            //参考WinUser.h
            if (uMsg > 1024) {
                int event = lParam.intValue();
                if(event == 514 || event == 517){
                    showContextMenu(hwnd);
                }
            }
            return user32.DefWindowProc(hwnd, uMsg, wParam, lParam);
        };
        wndclass.hInstance = Kernel32.INSTANCE.GetModuleHandle(null);
        user32.RegisterClassEx(wndclass);
        return user32.CreateWindowEx(0, className, "HiddenTrayWindow",
                0, 0, 0, 0, 0,
                null, null, wndclass.hInstance, null);
    }
    
    private static void showContextMenu(WinDef.HWND hwnd) {
        WinDef.HMENU hMenu = ExtendedUser32.INSTANCE.CreatePopupMenu();
        var success = ExtendedUser32.INSTANCE.AppendMenuW(hMenu, 0, new WinDef.WPARAM(1), "Switch Foreground/Background");
        if(!success) return;
        WinDef.POINT pt = new WinDef.POINT();
        ExtendedUser32.INSTANCE.GetCursorPos(pt);
        ExtendedUser32.INSTANCE.SetForegroundWindow(hwnd);
        
        int cmd = ExtendedUser32.INSTANCE.TrackPopupMenu(
                hMenu, 0x0120,//TPM_RETURNCMD | TPM_LEFTALIGN | TPM_BOTTOMALIGN,
                pt.x, pt.y, 0, hwnd, null
        );
        if(cmd == 1){
            VanillaUtils.ClientHandler.switchWindowState();
        }
    }
    
    private static WinDef.HICON createIcon() {
        var iconSet = SharedConstants.getCurrentVersion().isStable() ? IconSet.RELEASE : IconSet.SNAPSHOT;
        var iconList = ThrowableSupplier.getOrThrow(() -> iconSet.getStandardIcons(Minecraft.getInstance().getVanillaPackResources()));
        var icon = ThrowableSupplier.getOrThrow(() -> ImageIO.read(iconList.getLast().get()));
        //ThrowableSupplier.getOrThrow(() -> ImageIO.write(icon,".png", Path.of("icon").toFile()));
        var width = icon.getWidth();
        var height = icon.getHeight();
        int[] pixels = new int[width * height];
        icon.getRGB(0, 0, width, height, pixels, 0, width);
        
        WinDef.HDC hdc = User32.INSTANCE.GetDC(null);
        
        
        WinGDI.BITMAPINFO bmi = new WinGDI.BITMAPINFO(3);
        bmi.bmiHeader.biSize = bmi.bmiHeader.size();
        bmi.bmiHeader.biWidth = width;
        bmi.bmiHeader.biHeight = -height;
        bmi.bmiHeader.biPlanes = 1;
        bmi.bmiHeader.biBitCount = 32;
        bmi.bmiHeader.biCompression = WinGDI.BI_BITFIELDS;
        bmi.bmiColors[0] = new WinGDI.RGBQUAD();
        bmi.bmiColors[0].rgbRed = (byte)0xFF;
        bmi.bmiColors[1] = new WinGDI.RGBQUAD();
        bmi.bmiColors[1].rgbGreen = (byte)0xFF;
        bmi.bmiColors[2] = new WinGDI.RGBQUAD();
        bmi.bmiColors[2].rgbBlue = (byte)0xFF;
       
        PointerByReference ppvBits = new PointerByReference();
        WinDef.HBITMAP colorBitmap = ExtendedGDI32.INSTANCE.CreateDIBSection(hdc, bmi, WinGDI.DIB_RGB_COLORS, ppvBits, null, 0);
        
        ppvBits.getValue().write(0, pixels, 0, pixels.length);
        
       
        byte[] maskBits = new byte[(width * height) / 8];
        WinDef.HBITMAP maskBitmap = ExtendedGDI32.INSTANCE.CreateBitmap(width, height, 1, 1, maskBits);
        
        
        WinGDI.ICONINFO iconInfo = new WinGDI.ICONINFO();
        iconInfo.fIcon = true;
        iconInfo.hbmMask = maskBitmap;
        iconInfo.hbmColor = colorBitmap;
        
       
        WinDef.HICON hIcon = ExtendedUser32.INSTANCE.CreateIconIndirect(iconInfo);
        
        GDI32.INSTANCE.DeleteObject(colorBitmap);
        GDI32.INSTANCE.DeleteObject(maskBitmap);
        User32.INSTANCE.ReleaseDC(null, hdc);
        
        return hIcon;
    }
}
