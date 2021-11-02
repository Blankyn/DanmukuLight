package me.blankm.danmuku.control.dispatcher;

import me.blankm.danmuku.model.DanMuModel;
import me.blankm.danmuku.model.channel.DanMuChannel;

/**
 * Created by android_ls on 2016/12/7.
 */
public interface IDanMuDispatcher {

    void dispatch(DanMuModel iDanMuView, DanMuChannel[] danMuChannels);

}
