<template>
    <grid v-bind:class="{ striped: striped }"
          v-bind:style="'grid-template-columns: ' + columnsLayout">
        <ghead>
            <gr>
                <gh v-for="(header, index) in headers" v-bind:key="index">{{ header }}</gh>
                <gh v-if="$scopedSlots['side.actions']"></gh>
            </gr>
        </ghead>

        <gbody v-if="items">
            <!-- if there is no groups, then `rows` is an array with a single element that is `items` -->
            <template v-for="(group, index) in rows">
                <div v-if="index > 0" class="separator"></div>
                <gr v-if="$scopedSlots['row.group']" v-bind:ref="'group'+group.id" class="header">
                    <gh v-bind:style="'grid-row: span ' + (groupedRows(group).length * 2 + 3)">
                        <slot name="row.group" v-bind:value="group"></slot>
                    </gh>
                </gr>

                <gr v-for="(item, rowNum) in groupedRows(group)"
                    v-bind:key="item.id"
                    v-bind:ref="'row'+item.id"
                    v-bind:id="'row'+item.id">
                    <gd v-for="colNum in columns.length - ($scopedSlots['row.group'] ? 1 : 0)"
                        v-bind:key="colNum"
                        tabindex="0">
                        <slot v-bind:name="'column.' + colNum" v-bind:value="item"></slot>
                    </gd>

                    <gd v-if="$scopedSlots['side.actions']"
                        class="side action"
                        tabindex="0">
                        <slot v-if="!extendable" name="side.actions" v-bind:value="item"></slot>
                    </gd>

                    <gr v-if="extendable" class="extendable">
                        <gd v-for="(action, actNum) in actions"
                            v-bind:key="actNum"
                            class="action"
                            tabindex="0"
                            v-bind:style="'grid-column: span ' + action">
                            <slot v-bind:name="'action.' + (actNum + 1)" v-bind:value="item"></slot>
                        </gd>
                        <gd v-if="$scopedSlots['side.actions']"
                            tabindex="0"
                            class="side action">
                            <slot name="side.actions" v-bind:value="item"></slot>
                        </gd>
                    </gr>
                </gr>
            </template>

        </gbody>
    </grid>
</template>

<script>
    module.exports = {
        props: {
            extendable: {type: Boolean, default: false},
            striped: {type: Boolean, default: true},
            columns: {type: Array, required: true},
            headers: Array,
            items: Array,
            actions: Array,
            groupedItems: Function
        },
        computed: {
            rows( ) {
                if (this.groupedItems) {
                    return this.items;
                } else {
                    return [this.items];
                }
            },
            columnsLayout() {
                let layout = this.columns.join(' ');
                if (this.$scopedSlots['side.actions']) {
                    layout += ' min-content';
                }
                return layout;
            }
        },
        methods: {
            groupedRows(group) {
                if (this.groupedItems) {
                    return this.groupedItems(group);
                } else {
                    return group;
                }
            }
        }
    };
</script>