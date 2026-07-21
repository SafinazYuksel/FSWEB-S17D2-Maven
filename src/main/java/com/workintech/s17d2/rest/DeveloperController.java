package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.Developer;
import com.workintech.s17d2.model.Experience;
import com.workintech.s17d2.model.JuniorDeveloper;
import com.workintech.s17d2.model.MidDeveloper;
import com.workintech.s17d2.model.SeniorDeveloper;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/developers")
public class DeveloperController {

    public Map<Integer, Developer> developers;
    private Taxable taxable;

    @Autowired
    public DeveloperController(Taxable taxable) {
        this.taxable = taxable;
    }

    @PostConstruct
    public void init() {
        developers = new HashMap<>();
    }

    @GetMapping
    public List<Developer> getAllDevelopers() {
        return new ArrayList<>(developers.values());
    }

    @GetMapping("/{id}")
    public Developer getDeveloperById(@PathVariable int id) {
        return developers.get(id);
    }

    @PostMapping
    public ResponseEntity<Developer> addDeveloper(@RequestBody Developer developer) {
        Developer newDeveloper;
        double netSalary;

        switch (developer.getExperience()) {
            case JUNIOR:
                netSalary = developer.getSalary() - (developer.getSalary() * taxable.getSimpleTaxRate() / 100);
                newDeveloper = new JuniorDeveloper(developer.getId(), developer.getName(), netSalary);
                break;
            case MID:
                netSalary = developer.getSalary() - (developer.getSalary() * taxable.getMiddleTaxRate() / 100);
                newDeveloper = new MidDeveloper(developer.getId(), developer.getName(), netSalary);
                break;
            case SENIOR:
                netSalary = developer.getSalary() - (developer.getSalary() * taxable.getUpperTaxRate() / 100);
                newDeveloper = new SeniorDeveloper(developer.getId(), developer.getName(), netSalary);
                break;
            default:
                newDeveloper = developer;
        }

        developers.put(newDeveloper.getId(), newDeveloper);
        return new ResponseEntity<>(newDeveloper, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public Developer updateDeveloper(@PathVariable int id, @RequestBody Developer developer) {
        if (developers.containsKey(id)) {
            developers.put(id, developer);
            return developer;
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public Developer deleteDeveloper(@PathVariable int id) {
        return developers.remove(id);
    }
}