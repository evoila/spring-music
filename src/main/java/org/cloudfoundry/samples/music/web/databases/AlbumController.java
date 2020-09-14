package org.cloudfoundry.samples.music.web.databases;

import org.cloudfoundry.samples.music.domain.Album;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/albums")
public class AlbumController {
    private static final Logger logger = LoggerFactory.getLogger(AlbumController.class);

    private CrudRepository<Album, String> repository;

    @Autowired(required = false)
    public void setCrudRepository(CrudRepository<Album, String> repository) {
        this.repository = repository;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteById(@PathVariable String id) {
        logger.info("Deleting album " + id);
        repository.deleteById(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Album getById(@PathVariable String id) {
        logger.info("Getting album " + id);
        return repository.findById(id).orElse(null);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Album> albums() {
        return repository.findAll();
    }

    @RequestMapping(method = RequestMethod.PUT)
    public Album add(@RequestBody Album album) {
        logger.info("Adding album " + album.getId());
        return repository.save(album);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Album update(@RequestBody Album album) {
        logger.info("Updating album " + album.getId());
        return repository.save(album);
    }
}