<template>
    <tooltiped v-if="tooltip || tooltipDisabled"
               v-bind:value="(disabled && tooltipDisabled) ? tooltipDisabled : tooltip">
        <span v-bind:class="'waves-effect waves-light btn' + (disabled ? ' disabled' : '')"
              @click="disabled ? '' : $emit('press', $event)">
            <i v-if='icon' class="material-icons">{{icon}}</i>
            <template v-else>{{ label}}</template>
        </span>
    </tooltiped>
    <span v-else
          v-bind:class="'waves-effect waves-light btn' + (disabled ? ' disabled' : '')"
          @click="disabled ? '' : $emit('press', $event)">
        <i v-if='icon' class="material-icons">{{icon}}</i>
        <template v-else>{{ label}}</template>
    </span>
</template>

<script>
    module.exports = {
        props: ['tooltip', 'disabled', 'tooltipDisabled', 'icon', 'label']
    };
</script>