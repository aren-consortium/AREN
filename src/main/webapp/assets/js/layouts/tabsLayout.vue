<template>
    <div>
        <ul class="tabs"
            v-bind:style="{'grid-template-columns': (tabsTemplate ? tabsTemplate : 'repeat(' + tabs.length + ', max-content)')}">
            <li v-for="(tab, index) in tabs"
                v-if="!tab.hidden"
                v-bind:class="{tab: true, disabled: tab.disabled}">
                <a v-bind:class="{active: (index === activeIndex)}"
                   @click="changeTab(index)"
                   v-bind:href="tab.href ? tab.href : false">
                    <slot v-if="$scopedSlots['tab.'+(index+1)+'.header']"
                          v-bind:name="'tab.'+(index+1)+'.header'">
                    </slot>
                    <template v-else>
                        {{ typeof tab === "string" ? tab : tab.label }}
                    </template>
                </a>
            </li>
        </ul>

        <template v-for="(tab, index) in tabs">
            <slot v-if="index === activeIndex" v-bind:name="'tab.' + (index+1)"></slot>
        </template>
    </div>
</template>

<script>
    module.exports = {
        props: ['tabs', 'tabsTemplate', 'withLink'],
        data() {
            return {
                activeIndex: 0
            }
        },
        mounted() {
            if (this.withLink) {
                this.activeIndex = this.tabs.findIndex((t) => t.id === this.$route.hash.substring(1));
                if (this.activeIndex === -1) {
                    this.activeIndex = this.$route.hash.substring(1) * 1;
                }
                if (this.tabs[this.activeIndex].href) {
                    window.location = this.tabs[this.activeIndex].href;
                }
            }
        },
        methods: {
            changeTab(index) {
                if (!this.tabs[index].disabled && index !== this.activeIndex) {
                    this.activeIndex = index;
                    this.$emit('change-tab', index);
                    if (this.withLink) {
                        window.location.hash = '#' + (this.tabs[index].id || this.activeIndex);
                    }
                }
            }
        }
    }
</script>