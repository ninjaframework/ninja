package ninja;

import com.google.inject.Provider;

/**
 * Implementation of the filter chain
 */
class FilterChainImpl implements FilterChain {
    private final Provider<? extends Filter> filterProvider;
    private final FilterChain next;

    FilterChainImpl(Provider<? extends Filter> filterProvider, FilterChain next) {
        this.filterProvider = filterProvider;
        this.next = next;
    }

    @Override
    public Result next(Context context) {
        return filterProvider.get().filter(next, context);
    }
}
