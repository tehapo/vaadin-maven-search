package com.vaadin.demo;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderer.HtmlRenderer;

@Theme("maven-search")
@Widgetset("com.vaadin.DefaultWidgetSet")
@SuppressWarnings("serial")
public class MavenSearchUI extends UI {

    private Grid grid;
    private TextField search;
    private VerticalLayout layout;

    @Override
    protected void init(VaadinRequest request) {
        layout = new VerticalLayout();
        layout.setHeight("600px");
        layout.setSpacing(true);
        setContent(layout);

        search = new TextField();
        search.setInputPrompt("Search for Maven packages...");
        search.setWidth("100%");
        search.addTextChangeListener(new TextChangeListener() {

            @Override
            public void textChange(TextChangeEvent event) {
                doSearch(event);
            }

        });

        grid = new Grid();
        grid.setVisible(false);
        layout.addComponents(search, grid);
        layout.setExpandRatio(grid, 1.0f);
    }

    private void doSearch(TextChangeEvent event) {
        String searchTerms = event.getText();
        if (searchTerms.length() < 3) {
            return;
        }

        // Create a new Grid to workaround an NPE caused by updating container
        // data source.
        Grid newGrid = new Grid();
        newGrid.addStyleName("search-results");
        newGrid.setSizeFull();
        newGrid.setSelectionMode(SelectionMode.NONE);
        newGrid.setContainerDataSource(new LazySearchContainer(searchTerms));

        // Configure columns properly.
        newGrid.getColumn("g").setHeaderCaption("groupId").setWidth(318.0)
                .setRenderer(new HtmlRenderer(), new GroupIdHtmlConverter());
        newGrid.getColumn("a").setWidth(288).setHeaderCaption("artifactId");
        newGrid.getColumn("latestVersion").setHeaderCaption("version")
                .setWidth(180.0);
        newGrid.getColumn("timestamp").setHeaderCaption("updated")
                .setWidth(180.0)
                .setRenderer(new HtmlRenderer(), new UpdatedHtmlConverter());

        layout.replaceComponent(grid, newGrid);
        grid = newGrid;
    }
}
