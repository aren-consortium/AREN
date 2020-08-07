/***** Register Vue components *****/
Vue.component('base-layout', vueLoader('layouts/baseLayout'));
Vue.component('grid-layout', vueLoader('layouts/gridLayout'));
Vue.component('modal-layout', vueLoader('layouts/modalLayout'));
Vue.component('tabs-layout', vueLoader('layouts/tabsLayout'));
Vue.component('institution-select', vueLoader('components/widgets/institutionSelect'));
Vue.component('tooltiped', vueLoader('components/widgets/tooltiped'));
Vue.component('documented', vueLoader('components/widgets/documented'));
Vue.component('search-box', vueLoader('components/widgets/searchBox'));
Vue.component('action-button', vueLoader('components/widgets/actionButton'));
Vue.component('toggle-action-button', vueLoader('components/widgets/toggleActionButton'));
Vue.component('text-input', vueLoader('components/widgets/textInput'));

const VueModal = {
    data() {
        return {
            callback: false
        };
    },
    methods: {
        open(callback) {
            if (callback) {
                this.callback = callback;
            }
            this.$children[0].open = true;
            this.$emit('open');
            if (this.afterOpen) {
                this.afterOpen( );
            }
        },
        close(returnValue = false) {
            this.$children[0].open = false;
            this.$emit('close');
            if (this.callback) {
                this.callback(returnValue);
            }
            if (this.afterClose) {
                this.afterClose( );
        }
        },
        isOpen() {
            return this.$children[0].open;
        }
    }
};

const VueGrid = {
    methods: {
        getRowById(id) {
            let row = this.$children[0].$refs[id];
            if (Array.isArray(row)) {
                return row[0];
            }
            return row;
        },
        selectRow(id) {
            let row = this.getRowById(id);
            let el = row.querySelector("[tabindex]");
            el.focus();
            scrollCenter(document.documentElement, el);
        }
    }
};
