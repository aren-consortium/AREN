<template>
    <span tabindex="1" class="dropdown-button">
        <span class="btn-floating btn waves-effect waves-light">
            <template v-if="$root.user.firstName">
                {{ $root.user.firstName[0] }}.{{ $root.user.lastName[0] }}
            </template>
            <template v-else>
                {{ $root.user.username[0] }}.{{ $root.user.username[1] }}
            </template>
        </span>
        <div class="dropdown-content">
            <h4 class="dropdown-title"> {{ $root.user.fullName() }}</h4>
            <a class="waves-effect waves-light btn-small" @click="$root.$refs.passwdModal.open()"> {{ $t('change_password') }}</span>
                <a class="waves-effect waves-light btn-small" @click="$root.logout()">{{ $t('menu.logout') }}</a>
                </ul>
        </div>
    </span>
</template>

<script>
    module.exports = {
    };
</script>