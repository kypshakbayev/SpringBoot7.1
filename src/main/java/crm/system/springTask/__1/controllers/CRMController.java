package crm.system.springTask.__1.controllers;

import crm.system.springTask.__1.db.ApplicationRequest;
import crm.system.springTask.__1.db.Courses;
import crm.system.springTask.__1.repositories.ReqRepository;
import crm.system.springTask.__1.repositories.CoursesRepository;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Builder
@Controller
public class CRMController {

    @Autowired
    private ReqRepository reqRepository;

    @Autowired
    private CoursesRepository coursesRepository;

    @GetMapping(value = "/")
    public String index(Model model) {
        model.addAttribute("requests", reqRepository.findAll());
        return "index";
    }

    @GetMapping(value = "/add")
    public String addRequest(Model model) {
        model.addAttribute("courses", coursesRepository.findAll());
        return "add";
    }

    @PostMapping("/addRequest")
    public String addRequest(@RequestParam(name = "fio") String userName,
                             @RequestParam(name = "kursy") Long courseId,
                             @RequestParam(name = "comment") String commentary,
                             @RequestParam(name = "phone") String phone) {

        Courses selectedCourse = coursesRepository.findById(courseId).orElse(null);

            ApplicationRequest appReq = ApplicationRequest.builder()
                    .userName(userName)
                    .commentary(commentary)
                    .phone(phone)
                    .handled(false)
                    .courses(selectedCourse)
                    .build();
            reqRepository.save(appReq);
            return "redirect:/";
        }

    @GetMapping(value = "/details/{id}")
    public String detailsRequest(@PathVariable("id") Long id, Model model) {

        ApplicationRequest appReq = reqRepository.findById(id).orElse(null);
        if(appReq.isHandled() == false) {
            model.addAttribute("newRequest", "*** Новая необработанная заявка ***");
        } else {
            model.addAttribute("processedRequest", "Обработанная заявка");
        }
        model.addAttribute("request", appReq);
        return "details";
    }

    @PostMapping("/saveRequest")
    public String saveRequest(@RequestParam(name = "id") Long id) {
        ApplicationRequest appReq = reqRepository.findById(id).orElse(null);
        if(appReq != null) {
            appReq.setHandled(true);
            reqRepository.save(appReq);
        }
        return "redirect:/";
    }
    @PostMapping("/deleteRequest")
    public String deleteRequest(@RequestParam("id") Long id) {
        reqRepository.deleteById(id);
        return "redirect:/";
    }

    @GetMapping("/filter")
    public String filter(@RequestParam boolean handled, Model model) {
        model.addAttribute("requests", reqRepository.findByHandled(handled));
        return "index";
    }

}
