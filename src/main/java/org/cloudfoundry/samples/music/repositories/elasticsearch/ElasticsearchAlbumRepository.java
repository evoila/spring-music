package org.cloudfoundry.samples.music.repositories.elasticsearch;

import org.cloudfoundry.samples.music.domain.Album;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Profile("elasticsearch")
@Repository
public class ElasticsearchAlbumRepository implements CrudRepository<Album, String> {

    private ElasticsearchRestTemplate template;

    ElasticsearchAlbumRepository(ElasticsearchRestTemplate template) {
        this.template = template;

        if (!template.indexOps(Album.class).exists()) {
            template.indexOps(Album.class).create();
        }
    }

    @Override
    public <S extends Album> S save(S entity) {
        return template.save(entity);
    }

    @Override
    public <S extends Album> Iterable<S> saveAll(Iterable<S> entities) {
        return template.save(entities);
    }

    @Override
    public Optional<Album> findById(String s) {
        return Optional.ofNullable(template.get(s, Album.class));
    }

    @Override
    public boolean existsById(String s) {
        return findById(s).isPresent();
    }

    @Override
    public Iterable<Album> findAll() {
        return template.search(Query.findAll(), Album.class).map(SearchHit::getContent).toList();
    }

    @Override
    public Iterable<Album> findAllById(Iterable<String> strings) {
        return StreamSupport.stream(strings.spliterator(), false).map(s -> findById(s).get()).collect(Collectors.toList());
    }

    @Override
    public long count() {
        return template.count(Query.findAll(), Album.class);
    }

    @Override
    public void deleteById(String s) {
        template.delete(s, Album.class);
    }

    @Override
    public void delete(Album entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteAll(Iterable<? extends Album> entities) {
        entities.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        deleteAll(findAll());
    }
}
