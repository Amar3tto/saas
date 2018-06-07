package ru.kpfu.itis.controller.webhooks.stripe;

import com.stripe.model.Event;
import com.stripe.model.Plan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.kpfu.itis.WebConstants;
import ru.kpfu.itis.controller.BaseApiController;
import ru.kpfu.itis.service.SaasService;

@Controller
public class PlanController extends BaseApiController {

    @Autowired
    private SaasService saasService;

    private final Logger LOGGER = LoggerFactory.getLogger(PlanController.class);

    @RequestMapping(value = WebConstants.Stripe.PLAN_CREATED_URL, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> createPlan(@RequestBody String event) {
        LOGGER.info("Caught stripe event - Plan created");
        Plan plan = getPlanFromEvent(event);
        saasService.createPlan(new ru.kpfu.itis.model.Plan(), plan);
        LOGGER.info("Plan was saved, sending good response");
        return createGoodResponse();
    }

    @RequestMapping(value = WebConstants.Stripe.PLAN_UPDATED_URL, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> updatePlan(@RequestBody String event) {
        LOGGER.info("Caught stripe event - Plan updated");
        Plan plan = getPlanFromEvent(event);
        saasService.createPlan(saasService.findPlanByCode(plan.getId()), plan);
        LOGGER.info("Plan was updated, sending good response");
        return createGoodResponse();
    }

    @RequestMapping(value = WebConstants.Stripe.PLAN_DELETED_URL, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> deletePlan(@RequestBody String event) {
        LOGGER.info("Caught stripe event - Plan deleted");
        Plan plan = getPlanFromEvent(event);
        saasService.deletePlan(plan.getId());
        LOGGER.info("Plan was deleted, sending good response");
        return createGoodResponse();
    }

    private Plan getPlanFromEvent(String event) {
        Event eventObj = Event.GSON.fromJson(event, Event.class);
        return (Plan) eventObj.getData().getObject();
    }
}
