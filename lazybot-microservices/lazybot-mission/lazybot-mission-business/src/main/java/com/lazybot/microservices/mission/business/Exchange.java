package com.lazybot.microservices.mission.business;

import com.corundumstudio.socketio.SocketIOClient;
import com.lazybot.microservices.commons.model.Bot;
import com.lazybot.microservices.commons.model.Item;
import com.lazybot.microservices.commons.model.Order;
import com.lazybot.microservices.commons.model.Position;
import com.lazybot.microservices.mission.model.MissionTools;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Mission exchange items between two bots
 */
@Getter
@Setter
@ToString
public class Exchange<T> extends MissionTools<T> {
    SocketIOClient socketIOClient;

    public Exchange() throws NoSuchMethodException {
        initializeSteps();
    }

    public Exchange(SocketIOClient socketIOClient, int step) throws NoSuchMethodException {
        super.setStepActual(step);
        this.socketIOClient = socketIOClient;
        initializeSteps();
    }

    @Override
    public void initializeSteps() throws NoSuchMethodException {
        Class<Exchange> thisClass = Exchange.class;
        List<Method> list = new ArrayList<>();
        list.add(thisClass.getDeclaredMethod("isBotsHasItems", List.class));
        list.add(thisClass.getDeclaredMethod("botsGoToPos"));
        list.add(thisClass.getDeclaredMethod("botsLookEachOther"));
        list.add(thisClass.getDeclaredMethod("botsDropItems"));
        super.setSteps(list);
    }

    private void isBotsHasItems(List<Object> datas) throws Exception {
        Bot bot1 = (Bot) datas.get(0);
        Bot bot2 = (Bot) datas.get(1);
        boolean bot1HasItems = checkPossessions(bot1.getInventory().getSlots(), (List<Item>) datas.get(2));
        boolean bot2HasItems = checkPossessions(bot2.getInventory().getSlots(), (List<Item>) datas.get(3));

        // Bots has items
        if (!(bot1HasItems && bot2HasItems))
            throw new Exception("The bots don't have items to exchange");
    }

    private void botsGoToPos(List<Object> datas) throws Exception {
        Bot bot1 = (Bot) datas.get(0);
        Bot bot2 = (Bot) datas.get(1);

        isBotsHasItems(datas);

        Order<Position> orderBot1 = new Order<>(bot1.getUsername(), bot1.getPosition(), "exchange", super.getStepActual());
        socketIOClient.sendEvent("goToPos", orderBot1);
    }

    private void botsLookEachOther() {
    }

    private void botsDropItems() {
    }

    // === Check values ===
    private boolean checkPossessions(List<Item> botInventory, List<Item> itemsGaveByBot1) {
        for (Item item : itemsGaveByBot1) {
            if (!botInventory.contains(item))
                return false;
        }
        return true;
    }
}
