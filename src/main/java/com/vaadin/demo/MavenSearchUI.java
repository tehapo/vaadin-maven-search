package com.vaadin.demo;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickEvent;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickListener;
import com.vaadin.ui.renderers.HtmlRenderer;

@Theme("maven-search")
@Widgetset("com.vaadin.DefaultWidgetSet")
@SuppressWarnings("serial")
public class MavenSearchUI extends UI implements UriFragmentChangedListener {

    private Grid grid;
    private TextField search;
    private VerticalLayout layout;
    private Label noResultsLabel;
    private DependencyWindow pomWindow;

    private static final String URI_FRAGMENT_PREFIX = "search:";

    @WebServlet("/*")
    @VaadinServletConfiguration(productionMode = true, ui = MavenSearchUI.class)
    public static class AppServlet extends VaadinServlet {

    }

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

        noResultsLabel = new Label("");
        noResultsLabel.setVisible(false);
        noResultsLabel.addStyleName("no-results");

        layout.addComponents(search, grid, noResultsLabel);
        layout.setExpandRatio(grid, 1.0f);
        layout.setExpandRatio(noResultsLabel, 1.0f);

        // Try initial URI fragment and hook a listener.
        String uriFragment = Page.getCurrent().getUriFragment();
        String initialSearch = "vaadin";
        if (uriFragment != null && uriFragment.startsWith(URI_FRAGMENT_PREFIX)) {
            initialSearch = uriFragment.substring(URI_FRAGMENT_PREFIX.length());
        }
        doSearch(initialSearch, false);
        search.setValue(initialSearch);
        Page.getCurrent().addUriFragmentChangedListener(this);
    }

    @Override
    public void uriFragmentChanged(UriFragmentChangedEvent event) {
        if (event.getUriFragment() != null
                && event.getUriFragment().startsWith(URI_FRAGMENT_PREFIX)) {
            String searchTerms = event.getUriFragment().substring(
                    URI_FRAGMENT_PREFIX.length());

            doSearch(searchTerms);
            search.setValue(searchTerms);
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
        doSearch(searchTerms, true);
    }

    private void doSearch(String searchTerms, boolean updateUriFragment) {
        if (searchTerms == null || searchTerms.length() == 0) {
            return;
        }
        if (updateUriFragment) {
            updateUriFragment(searchTerms);
        }

        // Create a new Grid to workaround an NPE caused by updating container
        // data source.
        final Grid newGrid = new Grid();
        newGrid.addStyleName("search-results");
        newGrid.setSizeFull();
        newGrid.setSelectionMode(SelectionMode.NONE);
        newGrid.setContainerDataSource(new LazySearchContainer(searchTerms));

        // Configure columns properly.
        // @formatter:off
        newGrid.getColumn("g")
               .setHeaderCaption("groupId").setWidth(263.0)
               .setRenderer(new HtmlRenderer(), new GroupIdHtmlConverter());
        newGrid.getColumn("a")
               .setWidth(263)
               .setHeaderCaption("artifactId");
        newGrid.getColumn("latestVersion")
               .setHeaderCaption("version")
               .setWidth(110.0);
        newGrid.getColumn("timestamp")
               .setHeaderCaption("updated")
               .setWidth(150.0)
               .setRenderer(new HtmlRenderer(), new UpdatedHtmlConverter());
        newGrid.getColumn("javaDocUrl")
               .setHeaderCaption("").setWidth(90.0)
               .setRenderer(new HtmlRenderer(), new JavadocHtmlConverter());
        newGrid.getColumn("pomSnippet")
               .setHeaderCaption("").setWidth(90.0)
               .setRenderer(new ButtonRenderer(new RendererClickListener() {

                    @Override
                    public void click(RendererClickEvent event) {
                        String pom = (String) newGrid.getContainerDataSource()
                                .getItem(event.getItemId())
                                .getItemProperty("pomSnippet").getValue();

                        if (pomWindow == null || !pomWindow.isAttached()) {
                            pomWindow = new DependencyWindow();
                            getUI().addWindow(pomWindow);
                            pomWindow.center();
                            pomWindow.focus();
                        }
                        pomWindow.addDependency(pom);
                    }

                }), new PomHtmlConverter());
        // @formatter:on

        layout.replaceComponent(grid, newGrid);
        grid = newGrid;

        // Hide Grid if no results found and display a Label instead.
        grid.setVisible(grid.getContainerDataSource().size() > 0);
        noResultsLabel.setVisible(!grid.isVisible());
        noResultsLabel.setValue("No search results for " + searchTerms + ".");
    }

    private static class DependencyWindow extends Window {

        private Label pomLabel = new Label("", ContentMode.PREFORMATTED);
        private int pomCount;

        public DependencyWindow() {
            pomLabel.setSizeFull();
            setContent(pomLabel);
            addStyleName("pom-window");
            setWidth("600px");
            setHeight("200px");
            addCloseListener(new CloseListener() {

                @Override
                public void windowClose(CloseEvent e) {
                    pomLabel.setValue("");
                    pomCount = 0;
                }
            });
        }

        public void addDependency(String pomSnippet) {
            pomCount++;
            setCaption("Copy to your pom.xml (" + pomCount + ")");
            if (pomCount > 1) {
                pomSnippet = "\n" + pomSnippet;
            }
            pomLabel.setValue(pomLabel.getValue() + pomSnippet);
        }

    }
}
