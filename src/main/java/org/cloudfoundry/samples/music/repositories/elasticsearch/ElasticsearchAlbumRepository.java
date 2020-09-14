package org.cloudfoundry.samples.music.repositories.elasticsearch;

import org.cloudfoundry.samples.music.domain.Album;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
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

    private void waiting() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <S extends Album> S save(S entity) {
        S album = template.save(entity);
        return album;
    }

    @Override
    public <S extends Album> Iterable<S> saveAll(Iterable<S> entities) {
        Iterable<S> albums = template.save(entities);
        return albums;
    }

    @Override
    public Optional<Album> findById(String s) {
        waiting();
        return Optional.ofNullable(template.get(s, Album.class));
    }

    @Override
    public boolean existsById(String s) {
        return findById(s).isPresent();
    }

    @Override
    public Iterable<Album> findAll() {
        /*
         * Because ES takes some time to save/delete objects we give it some time before firing a GET request.
         */
        waiting();
        StringQuery stringQuery = new StringQuery(QueryBuilders.matchAllQuery().toString());
        stringQuery.setMaxResults((int) count());

        return template.search(stringQuery, Album.class).map(SearchHit::getContent)
                .toList();
    }

    @Override
    public Iterable<Album> findAllById(Iterable<String> strings) {
        waiting();
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
