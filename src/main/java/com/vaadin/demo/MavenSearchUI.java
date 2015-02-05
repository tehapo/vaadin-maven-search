package com.vaadin.demo;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderer.ButtonRenderer;
import com.vaadin.ui.renderer.ClickableRenderer.RendererClickEvent;
import com.vaadin.ui.renderer.ClickableRenderer.RendererClickListener;
import com.vaadin.ui.renderer.HtmlRenderer;

@Theme("maven-search")
@Widgetset("com.vaadin.DefaultWidgetSet")
@SuppressWarnings("serial")
public class MavenSearchUI extends UI implements UriFragmentChangedListener {

    private Grid grid;
    private TextField search;
    private VerticalLayout layout;

    private static final String URI_FRAGMENT_PREFIX = "search:";

    @Override
    protected void init(VaadinRequest request) {
        layout = new VerticalLayout();
        layout.setHeight("600px");
        layout.setSpacing(true);
        setContent(layout);

        search = new TextField();
        search.addStyleName("search");
        search.setIcon(FontAwesome.SEARCH);
        search.setInputPrompt("Search for Maven packages...");
        search.setWidth("100%");
        search.addTextChangeListener(new TextChangeListener() {

            @Override
            public void textChange(TextChangeEvent event) {
                doSearch(event.getText());
            }

        });

        grid = new Grid();
        grid.setVisible(false);
        layout.addComponents(search, grid);
        layout.setExpandRatio(grid, 1.0f);

        // Try initial URI fragment and hook a listener.
        String uriFragment = Page.getCurrent().getUriFragment();
        if (uriFragment != null && uriFragment.startsWith(URI_FRAGMENT_PREFIX)) {
            doSearch(uriFragment.substring(URI_FRAGMENT_PREFIX.length()));
        } else {
            doSearch("vaadin");
        }
        Page.getCurrent().addUriFragmentChangedListener(this);
    }

    @Override
    public void uriFragmentChanged(UriFragmentChangedEvent event) {
        if (event.getUriFragment().startsWith(URI_FRAGMENT_PREFIX)) {
            doSearch(event.getUriFragment().substring(
                    URI_FRAGMENT_PREFIX.length()));
        }
    }

    private void updateUriFragment(String searchTerms) {
        if (searchTerms == null || searchTerms.length() == 0) {
            Page.getCurrent().setUriFragment("", false);
        } else {
            String uriFragment = Page.getCurrent().getUriFragment();
            if (!(URI_FRAGMENT_PREFIX + searchTerms).equals(uriFragment)) {
                Page.getCurrent().setUriFragment(
                        URI_FRAGMENT_PREFIX + searchTerms, false);
            }
        }
    }

    private void doSearch(String searchTerms) {
        if (searchTerms.length() < 3) {
            return;
        }
        updateUriFragment(searchTerms);
        search.setValue(searchTerms);

        // Create a new Grid to workaround an NPE caused by updating container
        // data source.
        final Grid newGrid = new Grid();
        newGrid.addStyleName("search-results");
        newGrid.setSizeFull();
        newGrid.setSelectionMode(SelectionMode.NONE);
        newGrid.setContainerDataSource(new LazySearchContainer(searchTerms));

        // Configure columns properly.
        newGrid.getColumn("g").setHeaderCaption("groupId").setWidth(263.0)
                .setRenderer(new HtmlRenderer(), new GroupIdHtmlConverter());
        newGrid.getColumn("a").setWidth(263).setHeaderCaption("artifactId");
        newGrid.getColumn("latestVersion").setHeaderCaption("version")
                .setWidth(110.0);
        newGrid.getColumn("timestamp").setHeaderCaption("updated")
                .setWidth(150.0)
                .setRenderer(new HtmlRenderer(), new UpdatedHtmlConverter());
        newGrid.getColumn("javaDocUrl").setHeaderCaption("").setWidth(90.0)
                .setRenderer(new HtmlRenderer(), new JavaDocHtmlConverter());
        newGrid.getColumn("pomSnippet").setHeaderCaption("").setWidth(90.0)
                .setRenderer(new ButtonRenderer(new RendererClickListener() {

                    @Override
                    public void click(RendererClickEvent event) {
                        String pom = (String) newGrid.getContainerDataSource()
                                .getItem(event.getItemId())
                                .getItemProperty("pomSnippet").getValue();

                        Window pomWindow = new Window("Copy to your pom.xml");
                        pomWindow.addStyleName("pom-window");
                        pomWindow.setContent(new Label(pom,
                                ContentMode.PREFORMATTED));
                        getUI().addWindow(pomWindow);
                        pomWindow.center();
                        pomWindow.focus();
                    }

                }), new PomHtmlConverter());

        layout.replaceComponent(grid, newGrid);
        grid = newGrid;
    }
}
