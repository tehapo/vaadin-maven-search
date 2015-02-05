package com.vaadin.demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vaadin.addons.lazyquerycontainer.AbstractBeanQuery;
import org.vaadin.addons.lazyquerycontainer.BeanQueryFactory;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;
import org.vaadin.addons.lazyquerycontainer.LazyQueryDefinition;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;

import com.vaadin.demo.data.MavenArtifact;
import com.vaadin.demo.data.MavenSearchService;

public class LazySearchContainer extends LazyQueryContainer {

    public static final int PAGE_LENGTH = 80;

    public LazySearchContainer(String searchTerms) {
        super(new LazyQueryDefinition(false, PAGE_LENGTH, "id"),
                createQueryFactory(searchTerms));
        addContainerProperty("g", String.class, "");
        addContainerProperty("a", String.class, "");
        addContainerProperty("latestVersion", String.class, "");
        addContainerProperty("timestamp", Long.class, 0);
        addContainerProperty("javaDocUrl", String.class, "");
    }

    private static BeanQueryFactory<MavenSearchBeanQuery> createQueryFactory(
            String searchTerms) {
        BeanQueryFactory<MavenSearchBeanQuery> queryFactory = new BeanQueryFactory<MavenSearchBeanQuery>(
                MavenSearchBeanQuery.class);
        Map<String, Object> queryConf = new HashMap<>();
        queryConf.put("searchTerms", searchTerms);
        queryFactory.setQueryConfiguration(queryConf);
        return queryFactory;
    }

    public static class MavenSearchBeanQuery extends
            AbstractBeanQuery<MavenArtifact> {

        private final MavenSearchService service = new MavenSearchService();

        public MavenSearchBeanQuery(QueryDefinition definition,
                Map<String, Object> queryConfiguration,
                Object[] sortPropertyIds, boolean[] sortStates) {
            super(definition, queryConfiguration, sortPropertyIds, sortStates);
        }

        @Override
        protected MavenArtifact constructBean() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected List<MavenArtifact> loadBeans(int startIndex, int count) {
            return service.search(
                    (String) getQueryConfiguration().get("searchTerms"),
                    startIndex, count).getArtifacts();
        }

        @Override
        protected void saveBeans(List<MavenArtifact> arg0,
                List<MavenArtifact> arg1, List<MavenArtifact> arg2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return service
                    .search((String) getQueryConfiguration().get("searchTerms"),
                            0, 0).getResponse().getNumFound();
        }

    }

}
